package org.telegram.ui.Profile;


import static org.telegram.messenger.AndroidUtilities.dp;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.RenderEffect;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileButtons extends LinearLayout {

    private final Map<Integer, ActionItem> allActionsMap = new HashMap<Integer, ActionItem>() {{
        put(ActionType.RESTART, new ActionItem(R.drawable.msg_retry, R.string.BizBotStart, ActionType.RESTART));
        put(ActionType.STOP, new ActionItem(R.drawable.block, R.string.Stop, ActionType.STOP));
        put(ActionType.MUTE, new ActionItem(R.drawable.mute, R.string.ChatsMute, ActionType.MUTE));
        put(ActionType.UNMUTE, new ActionItem(R.drawable.unmute, R.string.Unmute, ActionType.UNMUTE));
        put(ActionType.CALL, new ActionItem(R.drawable.call, R.string.Call, ActionType.CALL));
        put(ActionType.VIDEO_CALL, new ActionItem(R.drawable.video, R.string.GroupCallCreateVideo, ActionType.VIDEO_CALL));
        put(ActionType.VOICE_CHAT, new ActionItem(R.drawable.live_stream, R.string.StartVoipChatTitle, ActionType.VOICE_CHAT));
        put(ActionType.LIVE_STREAM, new ActionItem(R.drawable.live_stream, R.string.VoipChannelVoiceChat, ActionType.VOICE_CHAT));
        put(ActionType.GIFT, new ActionItem(R.drawable.gift, R.string.Gift2TitleProfile, ActionType.GIFT));
        put(ActionType.JOIN, new ActionItem(R.drawable.join, R.string.ChannelJoin, ActionType.JOIN));
        put(ActionType.LEAVE, new ActionItem(R.drawable.leave, R.string.VoipGroupLeave, ActionType.LEAVE));
        put(ActionType.MESSAGE, new ActionItem(R.drawable.message, R.string.TypeMessage, ActionType.MESSAGE));
        put(ActionType.DISCUSS, new ActionItem(R.drawable.message, R.string.Discussion, ActionType.DISCUSS));
        put(ActionType.REPORT, new ActionItem(R.drawable.report, R.string.Report2, ActionType.REPORT));
        put(ActionType.SHARE, new ActionItem(R.drawable.share_profile, R.string.VoipChatShare, ActionType.SHARE));
        put(ActionType.ADD_STORY, new ActionItem(R.drawable.story, R.string.AddStory, ActionType.ADD_STORY));
        put(ActionType.QR_CODE, new ActionItem(R.drawable.msg_qrcode, R.string.QrCode, ActionType.QR_CODE));
        put(ActionType.CHANGE_AVATAR, new ActionItem(R.drawable.filled_add_photo, R.string.SetPhoto, ActionType.CHANGE_AVATAR));
        put(ActionType.CHANGE_PROFILE_COLOR, new ActionItem(R.drawable.menu_profile_colors, R.string.NotificationsLedColor, ActionType.CHANGE_PROFILE_COLOR));
        put(ActionType.CHANGE_PROFILE_STATUS, new ActionItem(R.drawable.msg_status_set, R.string.Gift2Status, ActionType.CHANGE_PROFILE_STATUS));
    }};

    public void updateProfileColor(boolean isPremium, Theme.ResourcesProvider resourcesProvider) {
        int type = ActionType.CHANGE_PROFILE_COLOR;
        ActionItem actionItem = allActionsMap.get(type);
        if (actionItem == null) {
            return;
        }
        if (isPremium) {
            int menuProfileColors = R.drawable.menu_profile_colors;
            actionItem.setIcon(menuProfileColors);
            allActionsMap.put(type, actionItem);
        } else {
            Drawable icon = ContextCompat.getDrawable(getContext(), R.drawable.menu_profile_colors_locked);
            if (icon == null) {
                return;
            }
            icon.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
            Drawable lockIcon = ContextCompat.getDrawable(getContext(), R.drawable.msg_gallery_locked2);
            if (lockIcon == null) {
                return;
            }
            lockIcon.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(icon, lockIcon, dp(1), -dp(1)) {
                @Override
                public void setColorFilter(ColorFilter colorFilter) {}
            };
            actionItem.setIcon(combinedDrawable);
            allActionsMap.put(type, actionItem);
        }
    }
    public ProfileButtons(Context context) {
        super(context);
    }

    public interface ProfileAction {
        Integer getIcon();

        Drawable getIconDrawable();

        String getTitle();

        int getType();

        void setIcon(int icon);

        void setIcon(Drawable drawable);
    }

    public static class ActionItem implements ProfileAction {
        private Integer icon = null;
        private Drawable iconDrawable = null;
        private final String title;
        private final int actionType; // New field

        public ActionItem(int icon, int titleResourceId, int actionType) {
            this.icon = icon;
            this.title = LocaleController.getString(titleResourceId);
            this.actionType = actionType;
        }

        @Override
        public Integer getIcon() {
            return icon;
        }

        @Override
        public Drawable getIconDrawable() {
            return iconDrawable;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public int getType() {
            return actionType;
        }

        @Override
        public void setIcon(int icon) {
            this.icon = icon;
            this.iconDrawable = null;
        }

        @Override
        public void setIcon(Drawable drawable) {
            this.iconDrawable = drawable;
            this.icon = null;
        }

    }

    public static class ActionType {
        // Bot Actions
        public static final int MESSAGE = 0;
        public static final int MUTE = 1;
        public static final int UNMUTE = 2;
        public static final int SHARE = 3;
        public static final int STOP = 4;
        public static final int RESTART = 45;

        // Channel Actions
        public static final int JOIN = 5;
        public static final int GIFT = 7;
        public static final int DISCUSS = 9;
        public static final int LEAVE = 6;
        public static final int REPORT = 8;

        // Profile Actions
        public static final int CALL = 10;
        public static final int VIDEO_CALL = 12;

        // Group Actions
        public static final int VOICE_CHAT = 32;
        public static final int LIVE_STREAM = 33;
        public static final int ADD_STORY = 35;
        public static final int CHANGE_AVATAR = 36;
        public static final int QR_CODE = 37;
        public static final int CHANGE_PROFILE_COLOR = 38;
        public static final int CHANGE_PROFILE_STATUS = 39;
        public static final int CHANGE_PROFILE_STATUS_LOCKED = 40;
    }

    static class ProfileActionButton extends LinearLayout {
        private Paint backgroundPaint;
        private float cornerRadius;
        private int backgroundColor = Color.parseColor("#FFFFFF");

        public ProfileActionButton(Context context) {
            super(context);
            init();
        }

        public ProfileActionButton(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public ProfileActionButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
            setWillNotDraw(false);
            backgroundPaint = new Paint();
            backgroundPaint.setColor(Color.WHITE);
            backgroundPaint.setAntiAlias(true);
            cornerRadius = dp(8);
        }

        public void setBackgroundColor(int color) {
            this.backgroundColor = color;
            backgroundPaint.setColor(color);
            invalidate();
        }

        public void setCornerRadius(float radius) {
            this.cornerRadius = dp(radius);
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            RectF rect = new RectF(0, 0, getWidth(), getHeight());
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, backgroundPaint);
            super.onDraw(canvas);
        }
    }

    private static int MAIN_BLUE = Color.rgb(102, 142, 183);
    private static int BUTTON_BLUE = Color.rgb(84, 124, 168);

    @SuppressLint("ClickableViewAccessibility")
    private static final int iconSize = dp(20);

    private ProfileActionButton createIconWithLabel(Context context, ActionItem actionItem, Integer profileActionType, Callback<Integer, View, Context> onAction) {
//        container.setPadding(dp(8), dp(8), dp(8), dp(8));
        ProfileActionButton outsideContainer = new ProfileActionButton(context);
//        outsideContainer.setBackgroundColor(BUTTON_BLUE);
        Drawable rect = Theme.AdaptiveRipple.createRect(0, Theme.getColor(Theme.key_listSelector), 16);
        outsideContainer.setBackground(rect);
        outsideContainer.setBackgroundColor(BUTTON_BLUE);
        outsideContainer.setCornerRadius(20);
        outsideContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        outsideContainer.setGravity(Gravity.CENTER);

        LinearLayout innerContainer = new LinearLayout(context);
        innerContainer.setOrientation(LinearLayout.VERTICAL);
        innerContainer.setGravity(Gravity.CENTER);
        innerContainer.setPadding(dp(8), dp(8), dp(8), dp(8));

        ImageView imageView = new ImageView(context);
        Integer iconResId = actionItem.getIcon();
        Drawable drawable;
        if (iconResId != null) {
            drawable = ContextCompat.getDrawable(context, iconResId);
        } else {
            drawable = actionItem.getIconDrawable();
        }
        if (drawable != null) {
            drawable.setBounds(0, 0, iconSize, iconSize);
            imageView.setImageDrawable(drawable);
        }

        String text =actionItem.getTitle();
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);

        innerContainer.addView(imageView);
        innerContainer.addView(textView);

        outsideContainer.addView(innerContainer);

        outsideContainer.setOnTouchListener((v, event) -> {
            int duration = 100;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().alpha(0.8f).setDuration(duration).start();
                    return false;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().alpha(1.0f).setDuration(duration).start();
                    return false;
            }
            return false;
        });

        outsideContainer.setOnClickListener((v) -> {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(50);
                }
            }

            onAction.accept(profileActionType, v, context);
        });

        return outsideContainer;
    }

    private List<ProfileActionButton> buttons = new ArrayList<>();

    private Callback<Integer, View, Context> clickHandler;

    public interface Callback<T1, T2, T3> {
        void accept(T1 arg1s, T2 arg2, T3 arg3);
    }

    public void setClickHandler(Callback<Integer, View, Context> handler) {
        this.clickHandler = handler;
    }

    private static final int gap = dp(8);

    public void updateButtons(Context context, List<Integer> buttonsTypes) {
        removeAllViews();
        buttons.clear();

        int size = Math.min(buttonsTypes.contains(ActionType.CHANGE_PROFILE_STATUS) ? 5 : 4, buttonsTypes.size());
        for (int i = 0; i < size; i++) {
            Integer type = buttonsTypes.get(i);
            ActionItem actionItem = allActionsMap.get(type);
            if (actionItem == null) {
                continue;
            }
            ProfileActionButton button = createIconWithLabel(context, actionItem, type, this.clickHandler);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) button.getLayoutParams();
            params.leftMargin = (i == 0) ? 0 : gap;
            button.setLayoutParams(params);
            buttons.add(button);
            int[] outlocaation = new int[2];
            button.getLocationOnScreen(outlocaation);

            addView(button);
        }

    }
}
