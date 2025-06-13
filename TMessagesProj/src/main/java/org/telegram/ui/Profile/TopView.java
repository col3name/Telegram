package org.telegram.ui.Profile;

import static org.telegram.messenger.AndroidUtilities.dp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.widget.FrameLayout;

import androidx.core.graphics.ColorUtils;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.ChatActivityInterface;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.PeerColorActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stars.StarGiftPatterns;

@SuppressLint("ViewConstructor")
public class TopView extends FrameLayout {

    private final StarGiftPatterns starGiftPatterns = new StarGiftPatterns();

    private final ProfileActivity profileActivity;
    private int currentColor;
    private final Paint paint = new Paint();

    public TopView(Context context, ProfileActivity profileActivity) {
        super(context);
        setWillNotDraw(false);
        this.profileActivity = profileActivity;
//        this.fragment = fragment;
//        this.actionBar = actionBar;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec) + dp(3));
    }

    @Override
    public void setBackgroundColor(int color) {
        if (color != currentColor) {
            currentColor = color;
            paint.setColor(color);
            invalidate();
            if (!hasColorById) {
                profileActivity.setActionBarBackgroundColor(currentColor);
//                actionBarBackgroundColor = currentColor;
            }
        }
    }

    private boolean hasColorById;
    private final AnimatedFloat hasColorAnimated = new AnimatedFloat(this, 350, CubicBezierInterpolator.EASE_OUT_QUINT);
    public int color1, color2;
    private final AnimatedColor color1Animated = new AnimatedColor(this, 350, CubicBezierInterpolator.EASE_OUT_QUINT);
    private final AnimatedColor color2Animated = new AnimatedColor(this, 350, CubicBezierInterpolator.EASE_OUT_QUINT);

    private int backgroundGradientColor1, backgroundGradientColor2, backgroundGradientHeight;
    private LinearGradient backgroundGradient;
    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public void setBackgroundColorId(MessagesController.PeerColor peerColor, boolean animated) {
        if (peerColor != null) {
            hasColorById = true;
            color1 = peerColor.getBgColor1(Theme.isCurrentThemeDark());
            color2 = peerColor.getBgColor2(Theme.isCurrentThemeDark());
            profileActivity.setActionBarBackgroundColor(ColorUtils.blendARGB(color1, color2, 0.25f));
//            actionBarBackgroundColor = ColorUtils.blendARGB(color1, color2, 0.25f);
            if (peerColor.patternColor != 0) {
                emojiColor = peerColor.patternColor;
            } else {
                emojiColor = PeerColorActivity.adaptProfileEmojiColor(color1);
            }
        } else {
            profileActivity.setActionBarBackgroundColor(currentColor);
//            actionBarBackgroundColor = currentColor;
            hasColorById = false;
            if (AndroidUtilities.computePerceivedBrightness(profileActivity.getThemedColor(Theme.key_actionBarDefault)) > .8f) {
                emojiColor = profileActivity.getThemedColor(Theme.key_windowBackgroundWhiteBlueText);
            } else if (AndroidUtilities.computePerceivedBrightness(profileActivity.getThemedColor(Theme.key_actionBarDefault)) < .2f) {
                emojiColor = Theme.multAlpha(profileActivity.getThemedColor(Theme.key_actionBarDefaultTitle), .5f);
            } else {
                emojiColor = PeerColorActivity.adaptProfileEmojiColor(profileActivity.getThemedColor(Theme.key_actionBarDefault));
            }
        }
        if (!animated) {
            color1Animated.set(color1, true);
            color2Animated.set(color2, true);
        }
        invalidate();
    }

    private int emojiColor;
    private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable emoji = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this, false, dp(20), AnimatedEmojiDrawable.CACHE_TYPE_ALERT_PREVIEW_STATIC);

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        emoji.attach();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        emoji.detach();
    }

    public final AnimatedFloat emojiLoadedT = new AnimatedFloat(this, 0, 440, CubicBezierInterpolator.EASE_OUT_QUINT);
    public final AnimatedFloat emojiFullT = new AnimatedFloat(this, 0, 440, CubicBezierInterpolator.EASE_OUT_QUINT);

    private boolean hasEmoji;
    private boolean emojiIsCollectible;

    public void setBackgroundEmojiId(long emojiId, boolean isCollectible, boolean animated) {
        emoji.set(emojiId, animated);
        emoji.setColor(emojiColor);
        emojiIsCollectible = isCollectible;
        if (!animated) {
            emojiFullT.force(isCollectible);
        }
        hasEmoji = hasEmoji || emojiId != 0 && emojiId != -1;
        invalidate();
    }

    private boolean emojiLoaded;

    private boolean isEmojiLoaded() {
        if (emojiLoaded) {
            return true;
        }
        if (emoji != null && emoji.getDrawable() instanceof AnimatedEmojiDrawable) {
            AnimatedEmojiDrawable drawable = (AnimatedEmojiDrawable) emoji.getDrawable();
            if (drawable.getImageReceiver() != null && drawable.getImageReceiver().hasImageLoaded()) {
                return emojiLoaded = true;
            }
        }
        return false;
    }

    private Rect blurBounds = new Rect();

    private float minimiseProgress = 0;

    public void setMinimiseProgress(float progress) {
        minimiseProgress = Math.max(normalize(progress), 0f);
    }

    public  float normalize(float value) {
        final float minInput = 0.11f;
        final float maxInput = 1.0f;
        final float minOutput = 0.0f;

        if (maxInput == minInput) return minOutput;

        return (value - minInput) / (maxInput - minInput);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        final ActionBar actionBar2 = profileActivity.getActionBar();
        final int height = ActionBar.getCurrentActionBarHeight() + (actionBar2.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
        final float extraHeight = profileActivity.getExtraHeight();
        final float v = extraHeight + height + profileActivity.searchTransitionOffset;
        int y1 = (int) (v * (1.0f - profileActivity.mediaHeaderAnimationProgress));

        if (y1 != 0) {
            ChatActivityInterface previousTransitionFragment = profileActivity.previousTransitionFragment;
            if (previousTransitionFragment != null && previousTransitionFragment.getContentView() != null) {
                blurBounds.set(0, 0, getMeasuredWidth(), y1);
                BaseFragment previousTransitionMainFragment = profileActivity.previousTransitionMainFragment;
                if (previousTransitionFragment.getActionBar() != null && !previousTransitionFragment.getContentView().blurWasDrawn() && previousTransitionFragment.getActionBar().getBackground() == null) {
                    paint.setColor(Theme.getColor(Theme.key_actionBarDefault, previousTransitionFragment.getResourceProvider()));
                    canvas.drawRect(blurBounds, paint);
                } else if (previousTransitionMainFragment != null && previousTransitionMainFragment instanceof DialogsActivity && previousTransitionMainFragment.getFragmentView() instanceof SizeNotifierFrameLayout) {
                    previousTransitionMainFragment.getActionBar().blurScrimPaint.setColor(Theme.getColor(Theme.key_actionBarDefault, previousTransitionMainFragment.getResourceProvider()));
                    ((SizeNotifierFrameLayout) previousTransitionMainFragment.getFragmentView()).drawBlurRect(canvas, getY(), blurBounds, previousTransitionMainFragment.getActionBar().blurScrimPaint, true);
                } else {
                    previousTransitionFragment.getContentView().drawBlurRect(canvas, getY(), blurBounds, previousTransitionFragment.getActionBar().blurScrimPaint, true);
                }
            }
            paint.setColor(currentColor);
            final int color1 = color1Animated.set(this.color1);
            final int color2 = color2Animated.set(this.color2);
            final int gradientHeight = AndroidUtilities.statusBarHeight + dp(144);
            if (backgroundGradient == null || backgroundGradientColor1 != color1 || backgroundGradientColor2 != color2 || backgroundGradientHeight != gradientHeight) {
                backgroundGradient = new LinearGradient(0, 0, 0, backgroundGradientHeight = gradientHeight, new int[]{backgroundGradientColor2 = color2, backgroundGradientColor1 = color1}, new float[]{0, 1}, Shader.TileMode.CLAMP);
                backgroundPaint.setShader(backgroundGradient);
            }
            final float avatarAnimationProgress = profileActivity.getAvatarAnimationProgress();
            final float playProfileAnimationType = profileActivity.getPlayProfileAnimationType();
            final float progressToGradient = (playProfileAnimationType == 0 ? 1f : avatarAnimationProgress) * hasColorAnimated.set(hasColorById);
            if (progressToGradient < 1) {
                canvas.drawRect(0, 0, getMeasuredWidth(), y1, paint);
            }
            if (progressToGradient > 0) {
                backgroundPaint.setAlpha((int) (0xFF * progressToGradient));
                canvas.drawRect(0, 0, getMeasuredWidth(), y1, backgroundPaint);
            }
            if (hasEmoji) {
                final float loadedScale = emojiLoadedT.set(isEmojiLoaded());
                final float full = emojiFullT.set(emojiIsCollectible);
                if (loadedScale > 0) {
                    canvas.save();
                    canvas.clipRect(0, 0, getMeasuredWidth(), y1);
                    StarGiftPatterns.drawProfilePatternProgress(canvas, emoji, getMeasuredWidth(), ((actionBar2.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + dp(144)) - (1f - extraHeight / dp(88)) * dp(50), Math.min(1f, extraHeight / dp(88)), full, minimiseProgress);
                    starGiftPatterns.drawMetaBalls(canvas);

                    canvas.restore();
                }
            }
            if (previousTransitionFragment != null) {
                ActionBar actionBar = previousTransitionFragment.getActionBar();
                ActionBarMenu menu = actionBar.menu;
                if (actionBar != null && menu != null) {
                    int restoreCount = canvas.save();
                    canvas.translate(actionBar.getX() + menu.getX(), actionBar.getY() + menu.getY());
                    canvas.saveLayerAlpha(0, 0, menu.getMeasuredWidth(), menu.getMeasuredHeight(), (int) (255 * (1f - avatarAnimationProgress)), Canvas.ALL_SAVE_FLAG);
                    menu.draw(canvas);
                    canvas.restoreToCount(restoreCount);
                }
            }
        }
        if (y1 != v) {
            int color = profileActivity.getThemedColor(Theme.key_windowBackgroundWhite);
            paint.setColor(color);
            blurBounds.set(0, y1, getMeasuredWidth(), (int) v);
            float y = getY();
            if (profileActivity.previousTransitionFragment != null) {
                profileActivity.previousTransitionFragment.getContentView().drawBlurRect(canvas, y, blurBounds, paint, true);
            }

            // blurBounds.set(0, y1 + 100, getMeasuredWidth(), (int) v);
            // y += 100;
            // contentView.drawBlurRect(canvas, y, blurBounds, paint, true);
        }

        INavigationLayout parentLayout = profileActivity.getParentLayout();
        if (parentLayout != null) {
            parentLayout.drawHeaderShadow(canvas, (int) (profileActivity.getHeaderShadowAlpha() * 255), (int) v);
        }
    }

}