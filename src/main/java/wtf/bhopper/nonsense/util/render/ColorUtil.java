package wtf.bhopper.nonsense.util.render;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import wtf.bhopper.nonsense.util.misc.MathUtil;

import java.awt.*;

public class ColorUtil {

    public static final int NONSENSE = 0xFFFF5555;
    public static final int WHITE = 0xFFFFFFFF;
    public static final int BLACK = 0xFF000000;
    public static final int GRAY = 0xFFAAAAAA;
    public static final int RED = 0xFFFF0000;
    public static final int YELLOW = 0xFFFFFF00;
    public static final int GREEN = 0xFF00FF00;

    public static int dropShadow(int color) {
        return (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
    }

    public static int alpha(int color, int alpha) {
        return (color & 0xFFFFFF) | (alpha << 24);
    }


    public static int rainbow(float saturation, float brightness) {
        return rainbow(System.currentTimeMillis(), 0, saturation, brightness);
    }

    public static int rainbow(long timeMS, int count, float saturation, float brightness) {
        float hue = (float) ((timeMS - count * 200L) % 4001) / 4000.0F;
        return Color.HSBtoRGB(hue, saturation, brightness);
    }

    public static int exhiRainbow(long timeMS, int count) {
        float hue = (float) ((timeMS / 4L + (count * 9L)) % 256) / 255.0F;
        return Color.HSBtoRGB(hue, 0.55F, 0.9F);
    }

    public static int wave(int color, long timeMS, int count) {
        float factor = Math.abs((((timeMS * 2L) - count * 500L) % 8001) / 8000.0F - 0.5F) + 0.5F;
        Color awt = new Color(color);
        float[] hsb = Color.RGBtoHSB(awt.getRed(), awt.getGreen(), awt.getBlue(), null);
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2] * factor);
    }

    public static int astolfo(long timeMS, int count) {
        float hue = Math.abs(((((timeMS * 2L) - count * 500L) % 8001) / 8000.0F) - 0.5f) + 0.5F;
        return Color.HSBtoRGB(hue, 0.5F, 1.0F);
    }

    public static int health(float factor) {
        return Color.HSBtoRGB(factor / 3.0F, 1.0F, 1.0F);
    }

    public static int health(float health, float maxHealth) {
        return health(health / maxHealth);
    }

    public static int health(EntityLivingBase entity) {
        return health(entity.getHealth(), entity.getMaxHealth());
    }

    public static Color interpolate(Color current, Color target, int speed, float delta) {
        return new Color(
                MathUtil.incrementTo(current.getRed(), target.getRed(), (int) (speed * delta)),
                MathUtil.incrementTo(current.getGreen(), target.getGreen(), (int) (speed * delta)),
                MathUtil.incrementTo(current.getBlue(), target.getBlue(), (int) (speed * delta)),
                MathUtil.incrementTo(current.getAlpha(), target.getAlpha(), (int) (speed * delta))
        );
    }

    public static int darken(int color, int amount) {
        Color awt = new Color(color, true);
        for (int i = 0; i < amount; i++) {
            awt = awt.darker();
        }
        return awt.getRGB();
    }

    public static int multiplySatBri(int color, float s, float b) {
        Color awt = new Color(color, true);
        float[] hsb = Color.RGBtoHSB(awt.getRed(), awt.getGreen(), awt.getBlue(), null);
        hsb[1] = MathHelper.clamp_float(hsb[1] * s, 0.0F, 1.0F);
        hsb[2] = MathHelper.clamp_float(hsb[2] * b, 0.0F, 1.0F);
        return alpha(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]), awt.getAlpha());
    }

    public static int get(int red, int green, int blue, int alpha) {
        return (MathHelper.clamp_int(alpha, 0, 255) << 24)
                | (MathHelper.clamp_int(red, 0, 255) << 16)
                | (MathHelper.clamp_int(green, 0, 255) << 8)
                | (MathHelper.clamp_int(blue, 0, 255));
    }

    public static int getF(float r, float g, float b, float a) {
        return get((int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F), (int)(a * 255.0F));
    }

    public static float[] splitF(final int color) {
        final float r = (float) (color >> 16 & 255) / 255.0F;
        final float g = (float) (color >> 8 & 255) / 255.0F;
        final float b = (float) (color & 255) / 255.0F;
        final float a = (float) (color >> 24 & 255) / 255.0F;
        return new float[]{r, g, b, a};
    }


}
