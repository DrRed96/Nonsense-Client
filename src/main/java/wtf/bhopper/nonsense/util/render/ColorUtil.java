package wtf.bhopper.nonsense.util.render;

import wtf.bhopper.nonsense.util.misc.MathUtil;

import java.awt.*;

public class ColorUtil {

    public static final int NONSENSE = 0xFFFF5555;
    public static final int WHITE = 0xFFFFFFFF;
    public static final int BLACK = 0xFF000000;
    public static final int GRAY = 0xFFAAAAAA;

    public static int dropShadowColor(int color) {
        return (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
    }

    public static int alpha(int color, int alpha) {
        return (color & 0xFFFFFF) | (alpha << 24);
    }


    public static int rainbow(float saturation, float brightness) {
        return rainbow(System.currentTimeMillis(), 0, saturation, brightness);
    }

    public static int rainbow(long timeMS, int count, float saturation, float brightness) {
        float hue = (float) ((timeMS - count * 200L) % 4000) / 4000.0F;
        return Color.HSBtoRGB(hue, saturation, brightness);
    }

    public static int wave(int color, long timeMS, int count) {
        float factor = Math.abs((((timeMS * 2L) - count * 500L) % 8000) / 8000.0F - 0.5F) + 0.5F;
        Color awt = new Color(color);
        float[] hsb = Color.RGBtoHSB(awt.getRed(), awt.getGreen(), awt.getBlue(), null);
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2] * factor);
    }

    public static int astolfo(long timeMS, int count) {
        float hue = Math.abs(((((timeMS * 2L) - count * 500L) % 8000) / 8000.0F) - 0.5f) + 0.5F;
        return Color.HSBtoRGB(hue, 0.5F, 1.0F);
    }

    public static int health(float factor) {
        return Color.HSBtoRGB(factor / 3.0F, 1.0F, 1.0F);
    }

    public static int health(float health, float maxHealth) {
        return health(health / maxHealth);
    }

    public static Color interpolate(Color current, Color target, int speed, float delta) {
        return new Color(
                MathUtil.incrementTo(current.getRed(), target.getRed(), (int)(speed * delta)),
                MathUtil.incrementTo(current.getGreen(), target.getGreen(), (int)(speed * delta)),
                MathUtil.incrementTo(current.getBlue(), target.getBlue(), (int)(speed * delta)),
                MathUtil.incrementTo(current.getAlpha(), target.getAlpha(), (int)(speed * delta))
        );
    }

    public static int darken(int color, int amount) {
        Color awt = new Color(color, true);
        for (int i = 0; i < amount; i++) {
            awt = awt.darker();
        }
        return awt.getRGB();
    }


}
