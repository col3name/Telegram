package org.telegram.ui.Stars;

import static org.telegram.messenger.AndroidUtilities.dp;
import static org.telegram.messenger.AndroidUtilities.dpf2;
import static org.telegram.messenger.AndroidUtilities.pointTmp2;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.core.math.MathUtils;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.quickforward.QuickShareSelectorDrawable;

public class StarGiftPatterns {

    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_ACTION = 1;
    public static final int TYPE_GIFT = 2;
    public static final int TYPE_LINK_PREVIEW = 3;

    private static final float[][] patternLocations = new float[][]{
            {
                    83.33f, 24, 27.33f, .22f,
                    68.66f, 75.33f, 25.33f, .21f,
                    0, 86, 25.33f, .12f,
                    -68.66f, 75.33f, 25.33f, .21f,
                    -82.66f, 13.66f, 27.33f, .22f,
                    -80, -33.33f, 20, .24f,
                    -46.5f, -63.16f, 27, .21f,
                    1, -82.66f, 20, .15f,
                    46.5f, -63.16f, 27, .21f,
                    80, -33.33f, 19.33f, .24f,

                    115.66f, -63, 20, .15f,
                    134, -10.66f, 20, .18f,
                    118.66f, 55.66f, 20, .15f,
                    124.33f, 98.33f, 20, .11f,

                    -128, 98.33f, 20, .11f,
                    -108, 55.66f, 20, .15f,
                    -123.33f, -10.66f, 20, .18f,
                    -116, -63.33f, 20, .15f
            },
            {
                    27.33f, -57.66f, 20, .12f,
                    59, -32, 19.33f, .22f,
                    77, 4.33f, 22.66f, .2f,
                    100, 40.33f, 18, .12f,
                    58.66f, 59, 20, .18f,
                    73.33f, 100.33f, 22.66f, .15f,
                    75, 155, 22, .11f,

                    -27.33f, -57.33f, 20, .12f,
                    -59, -32.33f, 19.33f, .2f,
                    -77, 4.66f, 23.33f, .2f,
                    -98.66f, 41, 18.66f, .12f,
                    -58, 59.33f, 19.33f, .18f,
                    -73.33f, 100, 22, .15f,
                    -75.66f, 155, 22, .11f
            },
            {
                    -0.83f, -52.16f, 12.33f, .2f,
                    26.66f, -40.33f, 16, .2f,
                    44.16f, -20.5f, 12.33f, .2f,
                    53, 7.33f, 16, .2f,
                    31, 23.66f, 14.66f, .2f,
                    0, 32, 13.33f, .2f,
                    -29, 23.66f, 14, .2f,
                    -53, 7.33f, 16, .2f,
                    -44.5f, -20.16f, 12.33f, .2f,
                    -27.33f, -40.33f, 16, .2f,
                    43.66f, 50, 14.66f, .2f,
                    -41.66f, 48, 14.66f, .2f
            },
            {
                    -0.16f, -103.5f, 20.33f, .15f,
                    39.66f, -77.33f, 26.66f, .15f,
                    70.66f, -46.33f, 21.33f, .15f,
                    84.5f, -3.83f, 29.66f, .15f,
                    65.33f, 56.33f, 24.66f, .15f,
                    0, 67.66f, 24.66f, .15f,
                    -65.66f, 56.66f, 24.66f, .15f,
                    -85, -4, 29.33f, .15f,
                    -70.66f, -46.33f, 21.33f, .15f,
                    -40.33f, -77.66f, 26.66f, .15f,

                    62.66f, -109.66f, 21.33f, .11f,
                    103.166f, -67.5f, 20.33f, .11f,
                    110.33f, 37.66f, 20.66f, .11f,
                    94.166f, 91.16f, 20.33f, .11f,
                    38.83f, 91.16f, 20.33f, .11f,
                    0, 112.5f, 20.33f, .11f,
                    -38.83f, 91.16f, 20.33f, .11f,
                    -94.166f, 91.16f, 20.33f, .11f,
                    -110.33f, 37.66f, 20.66f, .11f,
                    -103.166f, -67.5f, 20.33f, .11f,
                    -62.66f, -109.66f, 21.33f, .11f
            }
    };

    public static void drawPattern(Canvas canvas, Drawable pattern, float w, float h, float alpha, float scale) {
        drawPattern(canvas, TYPE_DEFAULT, pattern, w, h, alpha, scale);
    }

    public static void drawPattern(Canvas canvas, int type, Drawable pattern, float w, float h, float alpha, float scale) {
        if (alpha <= 0.0f) return;
        for (int i = 0; i < patternLocations[type].length; i += 4) {
            final float x = patternLocations[type][i];
            final float y = patternLocations[type][i + 1];
            final float size = patternLocations[type][i + 2];
            final float thisAlpha = patternLocations[type][i + 3];

            float cx = x, cy = y, sz = size;
            if (w < h && type == TYPE_DEFAULT) {
                cx = y;
                cy = x;
            }
            cx *= scale;
            cy *= scale;
            sz *= scale;
            pattern.setBounds((int) (dp(cx) - dp(sz) / 2.0f), (int) (dp(cy) - dp(sz) / 2.0f), (int) (dp(cx) + dp(sz) / 2.0f), (int) (dp(cy) + dp(sz) / 2.0f));

            pattern.setAlpha((int) (0xFF * alpha * thisAlpha));
            pattern.draw(canvas);
        }
    }

    private static final float[] profileRight = new float[]{
            -35.66f, -5, 24, .2388f,
            -14.33f, -29.33f, 20.66f, .32f,
            -15, -73.66f, 19.33f, .32f,
            -2, -99.66f, 18, .1476f,
            -64.33f, -24.66f, 23.33f, .3235f,
            -40.66f, -53.33f, 24, .3654f,
            -50.33f, -85.66f, 20, .172f,
            -96, -1.33f, 19.33f, .3343f,
            -136.66f, -13, 18.66f, .2569f,
            -104.66f, -33.66f, 20.66f, .2216f,
            -82, -62.33f, 22.66f, .2562f,
            -131.66f, -60, 18, .1316f,
            -105.66f, -88.33f, 18, .1487f
    };
    private static final float[] profileLeft = new float[]{
            0, -107.33f, 16, .1505f,
            14.33f, -84, 18, .1988f,
            0, -50.66f, 18.66f, .3225f,
            13, -15, 18.66f, .37f,
            43.33f, 1, 18.66f, .3186f
    };

    public static void drawProfilePattern(Canvas canvas, Drawable pattern, float w, float h, float alpha, float full) {
        if (alpha <= 0.0f) return;

        final float b = h;
        final float l = 0, r = w;

        if (full > 0) {
            for (int i = 0; i < profileLeft.length; i += 4) {
                final float x = profileLeft[i];
                final float y = profileLeft[i + 1];
                final float size = profileLeft[i + 2];
                final float thisAlpha = profileLeft[i + 3];

                pattern.setBounds(
                        (int) (l + dpf2(x) - dpf2(size) / 2.0f),
                        (int) (b + dpf2(y) - dpf2(size) / 2.0f),
                        (int) (l + dpf2(x) + dpf2(size) / 2.0f),
                        (int) (b + dpf2(y) + dpf2(size) / 2.0f)
                );
                pattern.setAlpha((int) (0xFF * alpha * thisAlpha * full));
                pattern.draw(canvas);
            }

            final float sl = 77.5f, sr = 173.33f;
            final float space = w / AndroidUtilities.density - sl - sr;
            int count = Math.max(0, Math.round(space / 27.25f));
            if (count % 2 == 0) {
                count++;
            }
            for (int i = 0; i < count; ++i) {
                final float x = sl + space * ((float) i / (count - 1));
                final float y = i % 2 == 0 ? 0 : -12.5f;
                final float size = 17;
                final float thisAlpha = .21f;

                pattern.setBounds(
                        (int) (l + dpf2(x) - dpf2(size) / 2.0f),
                        (int) (b + dpf2(y) - dpf2(size) / 2.0f),
                        (int) (l + dpf2(x) + dpf2(size) / 2.0f),
                        (int) (b + dpf2(y) + dpf2(size) / 2.0f)
                );
                pattern.setAlpha((int) (0xFF * alpha * thisAlpha * full));
                pattern.draw(canvas);
            }
        }

        for (int i = 0; i < profileRight.length; i += 4) {
            final float x = profileRight[i];
            final float y = profileRight[i + 1];
            final float size = profileRight[i + 2];
            final float thisAlpha = profileRight[i + 3];

            pattern.setBounds(
                    (int) (r + dpf2(x) - dpf2(size) / 2.0f),
                    (int) (b + dpf2(y) - dpf2(size) / 2.0f),
                    (int) (r + dpf2(x) + dpf2(size) / 2.0f),
                    (int) (b + dpf2(y) + dpf2(size) / 2.0f)
            );
            pattern.setAlpha((int) (0xFF * alpha * thisAlpha));
            pattern.draw(canvas);
        }
    }

    private static final float[] dots = new float[]{
            // near circle
//            0, 0, 38, .3343f,
            -42.66f, -22, 38, .2388f,
            42.66f, -22, 38, .2388f,
            -42.66f, 26, 38, .2388f,
            42.66f, 26, 38, .2388f,
            -70.66f, -36, 38, .1316f,
            70.66f, -36, 38, .1316f,
            75.66f, 40, 38, .1316f,
            -75.66f, 40, 38, .1316f,
            0, -45.66f, 38, .1316f,
            0, 45.66f, 38, .1316f,
            // second
            -65.66f, 0, 38, .2388f,
            65.66f, 0, 38, .2388f,
            -100.66f, 0, 38, .1316f,
            100.66f, 0, 38, .1316f,
            -34.66f, -52, 38, .1316f,
            34.66f, -52, 38, .1316f,
            34.66f, 56, 38, .1316f,
            -34.66f, 56, 38, .1316f,
    };
    public static final int MAX = 120;
    private static final int GIFT_SIZE = 25;

    private static final RectF tmpRectF = new RectF();

    private static double findOtherLeg(double hypotenuse, double leg) {
        if (hypotenuse <= leg) {
            return 0;
        }

        return Math.sqrt(hypotenuse * hypotenuse - leg * leg);
    }
    private boolean ballsAllowed = true;

    public static PointF findIntersectionWithGravity(float x1, float y1, float radius1,
                                                     float x2, float y2, float radius2, boolean left) {
        float d = (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

        if (d > radius1 + radius2 || d < Math.abs(radius1 - radius2)) {
            return null;
        }

        float a = (radius1 * radius1 - radius2 * radius2 + d * d) / (2 * d);
        float h = (float) Math.sqrt(radius1 * radius1 - a * a);

        float px = x1 + a * (x2 - x1) / d;
        float py = y1 + a * (y2 - y1) / d;

        float intersection1X = px + h * (y2 - y1) / d;
        float intersection1Y = py - h * (x2 - x1) / d;

        float intersection2X = px - h * (y2 - y1) / d;
        float intersection2Y = py + h * (x2 - x1) / d;

        if (intersection1X != intersection2X) {
            if (intersection1X < intersection2X) {
                if (left) {
                    return new PointF(intersection1X, intersection1Y);
                } else {
                    return new PointF(intersection2X, intersection2Y);
                }
            }
        }


        if (intersection1Y > intersection2Y) {
            return new PointF(intersection1X, intersection1Y);
        } else {
            return new PointF(intersection2X, intersection2Y);
        }
    }

    private final RectF bubbleStart = new RectF();
    private final RectF buttonCurrent = new RectF();
    private final RectF bubbleCurrent = new RectF();
    private final RectF ballLeft = new RectF();
    private final RectF ballRight = new RectF();
    private float openProgress = 0f;

    public void setOpenProgress(float progress) {
        openProgress = progress;
    }

    private static final Path path = new Path();
    private final Paint paintBubbleBg = new Paint(Paint.ANTI_ALIAS_FLAG);

    public void drawMetaBalls(Canvas canvas) {
        final float radSg = fromTo(dp(5f), dp(3f), QuickShareSelectorDrawable.Interpolators.ballsRadius.getInterpolation(openProgress));
        float ncy = bubbleCurrent.bottom + radSg;
        float dx = (float) findOtherLeg(buttonCurrent.width() / 2f + radSg, Math.abs(ncy - buttonCurrent.centerY()));
        float ncx = buttonCurrent.centerX() - dx;

        boolean ballLeftOnCircle = ncx < bubbleCurrent.left + bubbleCurrent.height() / 2f;
        if (ballLeftOnCircle) {
            PointF f = findIntersectionWithGravity(
                    buttonCurrent.centerX(), buttonCurrent.centerY(), buttonCurrent.height() / 2f + radSg,
                    bubbleCurrent.left + bubbleCurrent.height() / 2f, bubbleCurrent.centerY(), bubbleCurrent.height() / 2f + radSg, true
            );
            if (f != null) {
                ncx = f.x;
                ncy = f.y;
            } else {
                ballsAllowed = false;
            }
        }
        ballLeft.set(ncx - radSg, ncy - radSg, ncx + radSg, ncy + radSg);

        ncy = bubbleCurrent.bottom + radSg;
        ncx = buttonCurrent.centerX() + dx;
        boolean ballRightOnCircle = ncx > bubbleCurrent.right - bubbleCurrent.height() / 2f;
        if (ballRightOnCircle) {
            PointF f = findIntersectionWithGravity(
                    buttonCurrent.centerX(), buttonCurrent.centerY(), buttonCurrent.height() / 2f + radSg,
                    bubbleCurrent.right - bubbleCurrent.height() / 2f, bubbleCurrent.centerY(), bubbleCurrent.height() / 2f + radSg, false
            );
            if (f != null) {
                ncx = f.x;
                ncy = f.y;
            } else {
                ballsAllowed = false;
            }
        }

        ballRight.set(ncx - radSg, ncy - radSg, ncx + radSg, ncy + radSg);

        float bdx = Math.abs(ballLeft.centerX() - ballRight.centerX());
        float bdy = Math.abs(ballLeft.centerY() - ballRight.centerY());

        boolean ballsConnected = Math.sqrt(bdx * bdx + bdy * bdy) <= (ballLeft.width() + ballRight.width()) / 2;
        if (ballsConnected && ballsAllowed) {
            ballsAllowed = false;
        }

        if (ballsAllowed) {
            buildPath(path, buttonCurrent, bubbleCurrent, ballLeft, ballRight, ballLeftOnCircle, ballRightOnCircle);
        }

        canvas.drawPath(path, paintBubbleBg);
    }

    private void buildPath(
            Path path,
            RectF buttonCurrent,
            RectF bubbleCurrent,
            RectF ballLeft,
            RectF ballRight,
            boolean ballLeftOnCircle,
            boolean ballRightOnCircle
    ) {
        path.reset();

        final float br = calculateAngle(
                buttonCurrent.centerX(),
                buttonCurrent.centerY(),
                ballRight.centerX(),
                ballRight.centerY()
        );

        final float bl = calculateAngle(
                buttonCurrent.centerX(),
                buttonCurrent.centerY(),
                ballLeft.centerX(),
                ballLeft.centerY()
        );

        arcTo(path, buttonCurrent, br, bl, false);
        final float bl2 = ballLeftOnCircle ? calculateAngle(
                ballLeft.centerX(),
                ballLeft.centerY(),
                bubbleCurrent.left + bubbleCurrent.height() / 2f,
                bubbleCurrent.centerY()
        ) : -90;

        arcTo(path, ballLeft, reverseAngle(bl), bl2, true, true);
        if (!ballLeftOnCircle) {
            path.lineTo(bubbleCurrent.left + bubbleCurrent.height() / 2f, bubbleCurrent.bottom);
        }
        tmpRectF.set(bubbleCurrent.left, bubbleCurrent.top, bubbleCurrent.left + bubbleCurrent.height(), bubbleCurrent.bottom);
        arcTo(path, tmpRectF, reverseAngle(bl2), -90, false);

        path.lineTo(bubbleCurrent.right - bubbleCurrent.height() / 2f, bubbleCurrent.top);

        final float br2 = ballRightOnCircle ? calculateAngle(
                ballRight.centerX(),
                ballRight.centerY(),
                bubbleCurrent.right - bubbleCurrent.height() / 2f,
                bubbleCurrent.centerY()
        ) : -90;

        tmpRectF.set(bubbleCurrent.right - bubbleCurrent.height(), bubbleCurrent.top, bubbleCurrent.right, bubbleCurrent.bottom);
        arcTo(path, tmpRectF, -90, reverseAngle(br2), false);
        if (!ballRightOnCircle) {
            path.lineTo(ballRight.centerX(), bubbleCurrent.bottom);
        }
        arcTo(path, ballRight, br2, reverseAngle(br), true, true);

        path.close();
    }


    private static float calculateAngle(float x1, float y1, float x2, float y2) {
        float deltaY = y2 - y1;
        float deltaX = x2 - x1;
        float angleRadians = (float) Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(angleRadians);
    }

    private static float fromTo(float start, float end, float progress) {
        return start + (end - start) * progress;
    }

    private static float reverseAngle(float angle) {
        if (angle <= 0) {
            return angle + 180;
        } else {
            return angle - 180;
        }
    }

    private void arcTo(Path path, RectF rectF, float startAngle, float endAngle, boolean clockwise) {
        arcTo(path, rectF, startAngle, endAngle, clockwise, false);
    }

    private void arcTo(Path path, RectF rectF, float startAngle, float endAngle, boolean clockwise, boolean strictMode) {
        float sweepAngle = endAngle - startAngle;

        if (clockwise) {
            if (sweepAngle > 0) {
                sweepAngle -= 360;
            }
        } else {
            if (sweepAngle < 0) {
                sweepAngle += 360;
            }
        }

        if (Math.abs(sweepAngle) > 270 && strictMode) {
            ballsAllowed = false;
        }

        path.arcTo(rectF, startAngle, sweepAngle);
    }
    private static final float[][] profileEmojiBackgroundPattern = new float[][] {
            // angle, radius, scale, alpha
            {4, 72f, 26f, .24f},

            {6, 112f, 22f, .17f},
    };
    private static final float[] profile2 = {
            // Angle | +Angle | Bias | Length

            // Top
            270     , 0  , +0.1f , 0.4f,
            // Top left
            270 - 30, -15, +0.2f, 0.575f,
            180 + 30, -15, 0     , 0.425f,
            180 + 30, 0  , -0.15f, 0.675f,
            // Left
            180     , -15, 0     , 0.5f,
            180     , -60, -0.05f, 0.8f,
            // Top right
            270 + 30, +15, +0.3f , 0.575f,
            270 + 60, +15, -0.02f, 0.425f,
            - 30, 0  , -0.18f, 0.675f,
            // Right
            0       , +15, +0.05f, 0.5f,
            0       , +60, -0.1f , 0.8f,
            // Bottom
            90      , 0  , -0.03f, 0.425f,
            // Bottom right
            90 - 30 , +15, 0.27f , 0.575f,
            90 - 60 , +45, -0.03f, 0.425f,
            30      , +45, -0.2f , 0.675f,
            // Bottom left
            90 +  30, -15, 0.25f , 0.575f,
            180 - 30 , -45, -0.04f, 0.425f,
            180 - 30, -45, -0.22f, 0.675f,
    };

    public static void drawProfilePatternProgress(Canvas canvas, Drawable pattern, float width, float height, float alpha, float full, float progress) {
        if (alpha <= 0.0f) return;
        final float centerX = (width / 2);
        final float centerY = height / 2;

        float avatarCx = width / 2f;
        float avatarCy = height / 2f - progress * MAX * 1.5f;
        float stableAvatarCy = height / 2f;
        float radius = dp(120);
        int size = AndroidUtilities.dp(24);
        for (int i = 0; i < profile2.length; i+=4) {
            float angle = profile2[i];
            float toAngle = angle + profile2[i + 1];
            float bias = profile2[i + 2];
            float len = profile2[i + 3];
            float fade = Math.max(1f - Math.min(Math.abs(90 - angle), 45f) / 45f, 1f - Math.min(Math.abs(270 - angle), 45f) / 45f);

            float start = Math.max(0, (len + bias) / 10f);
            float max = MathUtils.clamp(len + bias, 0.1f, 1f) * 0.8f;
            float v = (MathUtils.clamp((progress), start, max) - start) / (max - start);
            v = CubicBezierInterpolator.EASE_BOTH.getInterpolation(v);
            float a = (float) Math.toRadians(AndroidUtilities.lerpAngle(angle, toAngle, v));

            float l = len + len * full * 1.5f;
            float cx = (float) (avatarCx + Math.cos(a) * radius * l), cy = (float) (stableAvatarCy + Math.sin(a) * radius * l);
            cx = AndroidUtilities.lerp(cx, avatarCx, v);
            cy = AndroidUtilities.lerp(cy, avatarCy, v);

            float s = AndroidUtilities.lerp(1f + full * 0.75f, 0.4f, v) * (len >= 0.5f ? size * 0.9f : size);
            pattern.setAlpha((int) (0xFF * alpha * 0.6f * (1f - fade * 0.5f) * (1f - len)));
            pattern.setBounds((int) (cx - s / 2f), (int) (cy - s / 2f), (int) (cx + s / 2f), (int) (cy + s / 2f));
            pattern.draw(canvas);
        }
//        if (alpha <= 0.0f) return;
//
//        final float l = 0;
//
//        final float centerX = l + (width / 2);
//        final float centerY = height / 2;
//
//        final float progress = 1f - prg;
//        if (progress > 0) {
//            for (final float[] currentPattern : profileEmojiBackgroundPattern) {
//                final float radius = currentPattern[1] * progress;
//                final float size = currentPattern[2];
//                final float fractions = currentPattern[0];
//                pattern.setAlpha((int) (0xFF * alpha * currentPattern[3]));
//
//                for (float a = 0; a < 2 * Math.PI; a += (float) (Math.PI / fractions)) {
//                    final float x = (float) Math.sin(a) * radius;
//                    final float y = (float) Math.cos(a) * radius;
//
//                    pattern.setBounds(
//                            (int) (centerX + dpf2(x) - dpf2(size) / 2.0f),
//                            (int) (centerY + dpf2(y) - dpf2(size) / 2.0f),
//                            (int) (centerX + dpf2(x) + dpf2(size) / 2.0f),
//                            (int) (centerY + dpf2(y) + dpf2(size) / 2.0f)
//                    );
//                    pattern.draw(canvas);
//                }
//            }
//        }
//
//
//        float centerX = 132;
//        float centerY = 50;
//
//        for (int i = 0; i < dots.length; i += 4) {
//            float dotX = dots[i];
//            float dotY = dots[i + 1];
//            boolean isPriority = (dotX != 0 && dotY != 0) || dotX == 0;
//            float x = dotX + centerX;
//            float y = dotY + centerY - MAX * progress;
//            float ax = x;
//            float ay = y;
//            float coefficient = isPriority ? 2.7f : 2f;
//            float cy = centerY - MAX * progress;
//
//            float vx = ax - centerX;
//            float vy = ay - cy;
//            float radius = (float) Math.sqrt(vx * vx + vy * vy);
//            float angle = (float) Math.atan2(vy, vx) + (1f - progress) * 0.8f - (float) Math.toRadians(30);
//            float newRadius = radius * (1f - progress * coefficient);
//
//            x = centerX + (float) (newRadius * Math.cos(angle));
//            y = cy + (float) (newRadius * Math.sin(angle));
//
//            float size = Math.min(GIFT_SIZE, GIFT_SIZE * (1f - progress) * 1.5f);
//            int left = (int) (x - size / 2);
//            int top = (int) (y - size / 2);
//            float right = left + size;
//            float bottom = top + size;
//            pattern.setBounds(
//                    (int) (right + dpf2(x) - dpf2(size) / 2.0f),
//                    (int) (bottom + dpf2(y) - dpf2(size) / 2.0f),
//                    (int) (right + dpf2(x) + dpf2(size) / 2.0f),
//                    (int) (bottom + dpf2(y) + dpf2(size) / 2.0f)
//            );
//            float inverseAlpha = Math.max(0, 1.0f - (progress * coefficient));
//
//            pattern.setAlpha((int) (0xFF * dots[i + 3] * inverseAlpha));
//            pattern.draw(canvas);
////            float index = i / 4f;
////            boolean isFirstRange = index < 10;
////            float dotX = dots[i];
////            float dotY = dots[i + 1];
////            float shiftX = (-dotX) * progress;
////            float shiftY = (-dotY) * progress - progress * MAX - (isFirstRange ? MAX : MAX / 2f) * progress - 40 * progress;
////            float x = dotX + centerX;
////            float y = dotY + centerY;
////
////            float distance = getDistance(x, y, centerX, centerY);
////
////            float attractionForce = 1f - 1f / (distance * distance + 1e-6f);
////
////
////            float accelerateX = (isFirstRange ? 1.2f : (1f)) * attractionForce;
////            float accelerateY = (isFirstRange ? 1.2f : (1f)) * attractionForce;
////            x += shiftX * accelerateX;
////            y += shiftY * accelerateY;
////
////            float size = 25 * (1 - progress);
////
////            int left = (int) (x - size / 2);
////            int top = (int) (y - size / 2);
////            float right = left + size;
////            float bottom = top + size;
////
////            pattern.setBounds(
////                    (int) (right + dpf2(x) - dpf2(size) / 2.0f),
////                    (int) (bottom + dpf2(y) - dpf2(size) / 2.0f),
////                    (int) (right + dpf2(x) + dpf2(size) / 2.0f),
////                    (int) (bottom + dpf2(y) + dpf2(size) / 2.0f)
////            );
////
////            float baseAlpha = dots[i + 3];
////            float inverseAlpha = 1.0f - progress;
////
////            float finalAlpha = baseAlpha * inverseAlpha;
////
////            pattern.setAlpha((int) (0xFF * finalAlpha));
////            pattern.draw(canvas);
//        }
    }

}
