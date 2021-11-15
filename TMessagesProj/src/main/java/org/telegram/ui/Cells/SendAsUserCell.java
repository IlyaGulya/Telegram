package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;

public class SendAsUserCell extends FrameLayout {

    private BackupImageView avatarImageView;
    private LinearLayout nameStatusLayout;
    private SimpleTextView nameTextView;
    private SimpleTextView statusTextView;
    private AvatarDrawable avatarDrawable;
    private CheckBox2 checkBox;
    private Object currentObject;
    private CharSequence currentName;
    private CharSequence currentStatus;

    private int currentAccount = UserConfig.selectedAccount;

    private String lastName;
    private int lastStatus;
    private TLRPC.FileLocation lastAvatar;

    private int padding;

    public SendAsUserCell(Context context, int pad) {
        super(context);

        padding = pad;
        avatarDrawable = new AvatarDrawable();

        avatarImageView = new BackupImageView(context);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(24));
        final int leftAvatarMargin = LocaleController.isRTL ? 0 : (13 + padding);
        final int rightAvatarMargin = LocaleController.isRTL ? (13 + padding) : 0;
        final int avatarGravity = (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL;
        addView(avatarImageView, LayoutHelper.createFrame(38, 38, avatarGravity, leftAvatarMargin, 0, rightAvatarMargin, 0));

        nameStatusLayout = new LinearLayout(context);
        nameStatusLayout.setOrientation(LinearLayout.VERTICAL);
        nameStatusLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        final ShapeDrawable divider = new ShapeDrawable();
        divider.setIntrinsicHeight(AndroidUtilities.dp(5));
        divider.setAlpha(0);
        nameStatusLayout.setDividerDrawable(divider);

        final int rightTextMargin = (LocaleController.isRTL ? 72 : 28) + padding;
        final int leftTextMargin = (LocaleController.isRTL ? 28 : 72) + padding;
        nameStatusLayout.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        addView(nameStatusLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL, leftTextMargin, 0, rightTextMargin, 0));

        nameTextView = new SimpleTextView(context);
        nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        nameTextView.setTextSize(16);
        nameTextView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        nameStatusLayout.addView(nameTextView);

        statusTextView = new SimpleTextView(context);
        statusTextView.setTextSize(13);
        statusTextView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        nameStatusLayout.addView(statusTextView);

        checkBox = new CheckBox2(context, 21, null);
        checkBox.setColor(Theme.key_dialogRoundCheckBox, Theme.key_voipgroup_inviteMembersBackground, Theme.key_dialogRoundCheckBoxCheck);
        checkBox.setDrawUnchecked(false);
        checkBox.setDrawBackgroundAsArc(4);
        checkBox.setAlpha(0);
        checkBox.setProgressDelegate(progress -> {
            float scale = 1.0f - (1.0f - 0.789f) * checkBox.getProgress();
            avatarImageView.setScaleX(scale);
            avatarImageView.setScaleY(scale);
            invalidate();
        });
        addView(checkBox, LayoutHelper.createFrame(24, 24, avatarGravity, leftAvatarMargin, 0, rightAvatarMargin, 0));


        setWillNotDraw(false);
    }

    public void setObject(Object object, CharSequence name, CharSequence status) {
        currentObject = object;
        currentStatus = status;
        currentName = name;
        update(0);
    }

    public void setChecked(boolean checked, boolean animated) {
        checkBox.setChecked(checked, animated);
//        if (checkBox != null) {
//            checkBox.setChecked(checked, animated);
//        } else if (checkBoxType == 2) {
//            if (isChecked == checked) {
//                return;
//            }
//            isChecked = checked;
//            if (animator != null) {
//                animator.cancel();
//            }
//            if (animated) {
//                animator = ValueAnimator.ofFloat(0.0f, 1.0f);
//                animator.addUpdateListener(animation -> {
//                    float v = (float) animation.getAnimatedValue();
//                    float scale = isChecked ? 1.0f - 0.18f * v : 0.82f + 0.18f * v;
//                    avatarImageView.setScaleX(scale);
//                    avatarImageView.setScaleY(scale);
//                    checkProgress = isChecked ? v : 1.0f - v;
//                    invalidate();
//                });
//                animator.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        animator = null;
//                    }
//                });
//                animator.setDuration(180);
//                animator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
//                animator.start();
//            } else {
//                avatarImageView.setScaleX(isChecked ? 0.82f : 1.0f);
//                avatarImageView.setScaleY(isChecked ? 0.82f : 1.0f);
//                checkProgress = isChecked ? 1.0f : 0.0f;
//            }
//            invalidate();
//        }
    }

    public boolean isChecked() {
//        if (checkBox != null) {
//            return checkBox.isChecked();
//        }
        return checkBox.isChecked();
    }

    public Object getObject() {
        return currentObject;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int ourWidthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        final int ourHeight = AndroidUtilities.dp(56);
        final int ourHeightMeasureSpec = MeasureSpec.makeMeasureSpec(ourHeight, MeasureSpec.EXACTLY);
        super.onMeasure(ourWidthMeasureSpec, ourHeightMeasureSpec);
    }

    public void recycle() {
        avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void update(int mask) {
        if (currentObject == null) {
            return;
        }
        TLRPC.FileLocation photo = null;
        String newName = null;

//        if (currentStatus != null && TextUtils.isEmpty(currentStatus)) {
//            ((LayoutParams) nameTextView.getLayoutParams()).topMargin = AndroidUtilities.dp(19);
//        } else {
//            ((LayoutParams) nameTextView.getLayoutParams()).topMargin = AndroidUtilities.dp(10);
//        }
//        if (checkBox != null) {
//            ((LayoutParams) checkBox.getLayoutParams()).topMargin = AndroidUtilities.dp(33);
//            if (LocaleController.isRTL) {
//                ((LayoutParams) checkBox.getLayoutParams()).rightMargin = AndroidUtilities.dp(39);
//            } else {
//                ((LayoutParams) checkBox.getLayoutParams()).leftMargin = AndroidUtilities.dp(40);
//            }
//        }

        if (currentObject instanceof TLRPC.User) {
            TLRPC.User currentUser = (TLRPC.User) currentObject;
            if (currentUser.photo != null) {
                photo = currentUser.photo.photo_small;
            }
            if (mask != 0) {
                boolean continueUpdate = false;
                if ((mask & MessagesController.UPDATE_MASK_AVATAR) != 0) {
                    if (lastAvatar != null && photo == null || lastAvatar == null && photo != null || lastAvatar != null && photo != null && (lastAvatar.volume_id != photo.volume_id || lastAvatar.local_id != photo.local_id)) {
                        continueUpdate = true;
                    }
                }
                if (currentUser != null && currentStatus == null && !continueUpdate && (mask & MessagesController.UPDATE_MASK_STATUS) != 0) {
                    int newStatus = 0;
                    if (currentUser.status != null) {
                        newStatus = currentUser.status.expires;
                    }
                    if (newStatus != lastStatus) {
                        continueUpdate = true;
                    }
                }
                if (!continueUpdate && currentName == null && lastName != null && (mask & MessagesController.UPDATE_MASK_NAME) != 0) {
                    newName = UserObject.getUserName(currentUser);
                    if (!newName.equals(lastName)) {
                        continueUpdate = true;
                    }
                }
                if (!continueUpdate) {
                    return;
                }
            }
            avatarDrawable.setInfo(currentUser);
            lastStatus = currentUser.status != null ? currentUser.status.expires : 0;

            if (currentName != null) {
                lastName = null;
                nameTextView.setText(currentName, true);
            } else {
                lastName = newName == null ? UserObject.getUserName(currentUser) : newName;
                nameTextView.setText(lastName);
            }

            statusTextView.setVisibility(currentStatus == null ? GONE : VISIBLE);

            avatarImageView.setForUserOrChat(currentUser, avatarDrawable);
        } else {
            TLRPC.Chat currentChat = (TLRPC.Chat) currentObject;
            if (currentChat.photo != null) {
                photo = currentChat.photo.photo_small;
            }
            if (mask != 0) {
                boolean continueUpdate = false;
                if ((mask & MessagesController.UPDATE_MASK_AVATAR) != 0) {
                    if (lastAvatar != null && photo == null || lastAvatar == null && photo != null || lastAvatar != null && photo != null && (lastAvatar.volume_id != photo.volume_id || lastAvatar.local_id != photo.local_id)) {
                        continueUpdate = true;
                    }
                }
                if (!continueUpdate && currentName == null && lastName != null && (mask & MessagesController.UPDATE_MASK_NAME) != 0) {
                    newName = currentChat.title;
                    if (!newName.equals(lastName)) {
                        continueUpdate = true;
                    }
                }
                if (!continueUpdate) {
                    return;
                }
            }

            avatarDrawable.setInfo(currentChat);

            if (currentName != null) {
                lastName = null;
                nameTextView.setText(currentName, true);
            } else {
                lastName = newName == null ? currentChat.title : newName;
                nameTextView.setText(lastName);
            }

            if (currentStatus == null) {
                statusTextView.setTag(Theme.key_windowBackgroundWhiteGrayText);
                statusTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
                if (currentChat.participants_count != 0) {
                    if (ChatObject.isChannel(currentChat) && !currentChat.megagroup) {
                        statusTextView.setText(LocaleController.formatPluralString("Subscribers", currentChat.participants_count));
                    } else {
                        statusTextView.setText(LocaleController.formatPluralString("Members", currentChat.participants_count));
                    }
                } else if (currentChat.has_geo) {
                    statusTextView.setText(LocaleController.getString("MegaLocation", R.string.MegaLocation));
                } else if (TextUtils.isEmpty(currentChat.username)) {
                    if (ChatObject.isChannel(currentChat) && !currentChat.megagroup) {
                        statusTextView.setText(LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate));
                    } else {
                        statusTextView.setText(LocaleController.getString("MegaPrivate", R.string.MegaPrivate));
                    }
                } else {
                    if (ChatObject.isChannel(currentChat) && !currentChat.megagroup) {
                        statusTextView.setText(LocaleController.getString("ChannelPublic", R.string.ChannelPublic));
                    } else {
                        statusTextView.setText(LocaleController.getString("MegaPublic", R.string.MegaPublic));
                    }
                }
            }

            avatarImageView.setForUserOrChat(currentChat, avatarDrawable);
        }

        if (currentStatus != null) {
            statusTextView.setText(currentStatus, true);
            statusTextView.setTag(Theme.key_windowBackgroundWhiteGrayText);
            statusTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int cx = avatarImageView.getLeft() + avatarImageView.getMeasuredWidth() / 2;
        int cy = avatarImageView.getTop() + avatarImageView.getMeasuredHeight() / 2;
        Theme.checkboxSquare_checkPaint.setColor(Theme.getColor(Theme.key_dialogRoundCheckBox));
        Theme.checkboxSquare_checkPaint.setAlpha((int) (checkBox.getProgress() * 255));
        canvas.drawCircle(cx, cy, AndroidUtilities.dp(19), Theme.checkboxSquare_checkPaint);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }
}
