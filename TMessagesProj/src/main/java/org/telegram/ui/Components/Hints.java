package org.telegram.ui.Components;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class Hints {
    public static HintView addForwardRestrictedHintTo(FrameLayout parent,
                                                      @Nullable Theme.ResourcesProvider resourcesProvider,
                                                      boolean arrowOnTop,
                                                      TLRPC.Chat chat) {
        final int hintType = arrowOnTop ? HintView.TYPE_FORWARD_BUTTON_TOP : HintView.TYPE_FORWARD_BUTTON_BOTTOM;
        HintView hint = new HintView(parent.getContext(), hintType, arrowOnTop, resourcesProvider);
        final FrameLayout.LayoutParams layoutParams = LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 10, 0, 10, 0);
        parent.addView(hint, layoutParams);
        hint.setAlpha(0.0f);
        hint.setVisibility(View.INVISIBLE);
        hint.setTextPaddings(AndroidUtilities.dp(7), AndroidUtilities.dp(11));
        hint.setTextMaxWidth(parent.getWidth() - AndroidUtilities.dp(11) - AndroidUtilities.dp(17));
        if (ChatObject.isChannel(chat)) {
            hint.setText(LocaleController.getString("ForwardsFromChannelRestricted", R.string.ForwardsFromChannelRestricted));
        } else {
            hint.setText(LocaleController.getString("ForwardsFromGroupRestricted", R.string.ForwardsFromGroupRestricted));
        }

        return hint;
    }
}
