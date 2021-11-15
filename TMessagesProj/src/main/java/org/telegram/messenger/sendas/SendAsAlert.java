package org.telegram.messenger.sendas;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.SendAsUserCell;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class SendAsAlert {
    public static ActionBarPopupWindow open(SizeNotifierFrameLayout contentView,
                                            ChatActivityEnterView enterView,
                                            ArrayList<TLRPC.Peer> peers,
                                            TLRPC.Peer selectedPeer,
                                            Consumer<TLRPC.Peer> onPeerSelected,
                                            PopupWindow.OnDismissListener onDismiss) {
        if (peers.isEmpty()) {
            return null;
        }
        int totalHeight = contentView.getHeightWithKeyboard();
        int availableHeight = totalHeight - AndroidUtilities.dp(46 + 16);

        Drawable shadowDrawable2 = ContextCompat.getDrawable(contentView.getContext(), R.drawable.popup_fixed_alert).mutate();
        shadowDrawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground), PorterDuff.Mode.MULTIPLY));

        AtomicReference<ActionBarPopupWindow> popupRef = new AtomicReference<>();

        LinearLayout linearLayout = new LinearLayout(contentView.getContext()) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(260), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(416), MeasureSpec.AT_MOST));
                setPivotX(AndroidUtilities.dp(8));
                setPivotY(getMeasuredHeight() - AndroidUtilities.dp(8));
            }

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    final ActionBarPopupWindow actionBarPopupWindow = popupRef.get();
                    if (actionBarPopupWindow != null) {
                        actionBarPopupWindow.dismiss();
                    }
                }
                return super.dispatchKeyEvent(event);
            }
        };
        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            private int[] pos = new int[2];

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final ActionBarPopupWindow popup = popupRef.get();
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    if (popup != null && popup.isShowing()) {
                        View contentView = popup.getContentView();
                        contentView.getLocationInWindow(pos);
                    }
                } else if (event.getActionMasked() == MotionEvent.ACTION_OUTSIDE) {
                    if (popup != null && popup.isShowing()) {
                        popup.dismiss();
                    }
                }
                return false;
            }
        });
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        RecyclerListView listView = new RecyclerListView(contentView.getContext());
        listView.setLayoutManager(new LinearLayoutManager(contentView.getContext()));
        listView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int p = parent.getChildAdapterPosition(view);
                if (p == 0) {
                    outRect.top = AndroidUtilities.dp(4);
                }
                if (p == peers.size() - 1) {
                    outRect.bottom = AndroidUtilities.dp(4);
                }
            }
        });

        final ListAdapter adapter = new ListAdapter(contentView.getContext(), peers, UserConfig.selectedAccount);
        adapter.setSelectedPeer(selectedPeer);
        listView.setAdapter(adapter);
        listView.setSectionsType(2);


        final int headerHeight = AndroidUtilities.dp(40);
        final int itemsHeight = AndroidUtilities.dp(56) * (listView.getAdapter().getItemCount() - 1);
        int listViewTotalHeight = headerHeight + itemsHeight + AndroidUtilities.dp(8);

        linearLayout.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 320, 0, 0, 0, 0));

        if (listViewTotalHeight > availableHeight) {
            if (availableHeight > AndroidUtilities.dp(620)) {
                listView.getLayoutParams().height = AndroidUtilities.dp(620);
            } else {
                listView.getLayoutParams().height = availableHeight;
            }
        } else {
            listView.getLayoutParams().height = listViewTotalHeight;
        }

        Drawable shadowDrawable3 = ContextCompat.getDrawable(contentView.getContext(), R.drawable.popup_fixed_alert).mutate();
        shadowDrawable3.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground), PorterDuff.Mode.MULTIPLY));
        linearLayout.setBackground(shadowDrawable3);

        ActionBarPopupWindow popup = new ActionBarPopupWindow(linearLayout, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
        popupRef.set(popup);
        popup.setOnDismissListener(onDismiss);
        popup.setOutsideTouchable(true);
        popup.setClippingEnabled(true);
        popup.setFocusable(true);
        popup.setInputMethodMode(ActionBarPopupWindow.INPUT_METHOD_NOT_NEEDED);
        popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        popup.getContentView().setFocusableInTouchMode(true);

        int y = enterView.getMeasuredHeight() + AndroidUtilities.dp(6);
        popup.showAtLocation(contentView, Gravity.LEFT | Gravity.BOTTOM, AndroidUtilities.dp(6), y);

        linearLayout.setAlpha(0f);
        linearLayout.setScaleX(0f);
        linearLayout.setScaleY(0f);
        linearLayout.animate().alpha(1f).scaleX(1f).scaleY(1f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(350);

        listView.setOnItemClickListener((view1, position) -> {
            TLRPC.Peer peer = adapter.peers.get(position);
            adapter.setSelectedPeer(peer);

            if (view1 instanceof SendAsUserCell) {
                ((SendAsUserCell) view1).setChecked(true, true);
            }
            for (int a = 0, N = listView.getChildCount(); a < N; a++) {
                View child = listView.getChildAt(a);
                if (child != view1) {
                    if (child instanceof SendAsUserCell) {
                        ((SendAsUserCell) child).setChecked(false, true);
                    }
                }
            }

            listView.postDelayed(() -> {
                onPeerSelected.accept(peer);
            }, 150);
        });

        return null;
    }

    private static class ListAdapter extends RecyclerListView.SectionsAdapter {
        private static final int POSITION_HEADER = 0;

        private static final int ITEM_HEADER = 0;
        private static final int ITEM_PEER = 1;

        private Context context;
        private ArrayList<TLRPC.Peer> peers;
        private TLRPC.Peer selectedPeer;
        private int currentAccount;

        public ListAdapter(Context context, ArrayList<TLRPC.Peer> peers, int currentAccount) {
            this.context = context;
            this.peers = peers;
            this.currentAccount = currentAccount;
        }

        public void setSelectedPeer(TLRPC.Peer selectedPeer) {
            this.selectedPeer = selectedPeer;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == ITEM_HEADER) {
                view = new HeaderCell(context, Theme.key_windowBackgroundWhiteBlueHeader, 16, 0, false);
                view.setMinimumHeight(AndroidUtilities.dp(40));
            } else {
                view = new SendAsUserCell(parent.getContext(), 0);
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            long did = MessageObject.getPeerId(selectedPeer);
            if (holder.itemView instanceof SendAsUserCell) {
                SendAsUserCell cell = (SendAsUserCell) holder.itemView;
                Object object = cell.getObject();
                long id = 0;
                if (object != null) {
                    if (object instanceof TLRPC.Chat) {
                        id = -((TLRPC.Chat) object).id;
                    } else {
                        id = ((TLRPC.User) object).id;
                    }
                }
                cell.setChecked(did == id, true);
            }
        }

        @Override
        public int getSectionCount() {
            return 1;
        }

        @Override
        public int getCountForSection(int section) {
            return peers.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder, int section, int row) {
            return holder.itemView instanceof SendAsUserCell;
        }

        @Override
        public int getItemViewType(int section, int position) {
            return POSITION_HEADER == position ? ITEM_HEADER : ITEM_PEER;
        }

        @Override
        public Object getItem(int section, int position) {
            return peers.get(position - 1);
        }

        @Override
        public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof SendAsUserCell) {
                ((SendAsUserCell) holder.itemView).recycle();
            }
        }

        @Override
        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            if (position == POSITION_HEADER) {
                HeaderCell cell = (HeaderCell) holder.itemView;
                cell.setText(LocaleController.getString("SendAsPopupTitle", R.string.SendAsPopupTitle));
            } else {
                long did = MessageObject.getPeerId(peers.get(position));
                TLObject object;
                String status;
                if (did > 0) {
                    object = MessagesController.getInstance(currentAccount).getUser(did);
                    status = LocaleController.getString("SendAsPopupPersonalAccount", R.string.SendAsPopupPersonalAccount);
                } else {
                    final TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(-did);
                    object = chat;
                    status = LocaleController.formatPluralString("Subscribers", chat.participants_count);
                }
                SendAsUserCell cell = (SendAsUserCell) holder.itemView;
                cell.setObject(object, null, status);
            }
        }

        @Override
        public View getSectionHeaderView(int section, View view) {
            if (view == null) {
                view = new HeaderCell(context, Theme.key_windowBackgroundWhiteBlueHeader, 16, 0, false);
                view.setMinimumHeight(AndroidUtilities.dp(40));
                view.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(5), Color.WHITE));
                final ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(params);
            }
            HeaderCell cell = (HeaderCell) view;
            cell.setText(LocaleController.getString("SendAsPopupTitle", R.string.SendAsPopupTitle));
            return cell;
        }

        @Override
        public String getLetter(int position) {
            return null;
        }

        @Override
        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            position[0] = 0;
            position[1] = 0;
        }
    }
}
