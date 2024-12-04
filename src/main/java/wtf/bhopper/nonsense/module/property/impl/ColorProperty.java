package wtf.bhopper.nonsense.module.property.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.MathHelper;
import wtf.bhopper.nonsense.module.property.Property;
import wtf.bhopper.nonsense.util.render.ColorUtil;

import java.awt.*;
import java.util.function.Supplier;

public class ColorProperty extends Property<Color> {

    public ColorProperty(String displayName, String description, int value, Supplier<Boolean> dependency) {
        super(displayName, description, new Color(value, true), dependency);
    }

    public ColorProperty(String displayName, String description, int value) {
        this(displayName, description, value, () -> true);
    }

    public void setRGB(int rgba) {
        this.set(new Color(rgba, true));
    }

    public void setRGB(int red, int green, int blue, int alpha) {
        this.set(new Color(red, green, blue, alpha));
    }

    public void setHSB(float hue, float saturation, float brightness, int alpha) {
        this.set(new Color(ColorUtil.alpha(Color.HSBtoRGB(hue, saturation, brightness), alpha), true));
    }

    public void setHSB(float[] hsb) {
        this.set(new Color(ColorUtil.alpha(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]), this.getAlpha()), true));
    }

    public void setHue(float hue) {
        float[] hsb = this.getHSB();
        hsb[0] = MathHelper.clamp_float(hue, 0.0F, 1.0F);
        this.setHSB(hsb);
    }

    public void setSaturation(float saturation) {
        float[] hsb = this.getHSB();
        hsb[1] = MathHelper.clamp_float(saturation, 0.0F, 1.0F);
        this.setHSB(hsb);
    }

    public void setBrightness(float brightness) {
        float[] hsb = this.getHSB();
        hsb[2] = MathHelper.clamp_float(brightness, 0.0F, 1.0F);
        this.setHSB(hsb);
    }

    public void setAlpha(float alpha) {
        float[] hsb = this.getHSB();
        this.setHSB(hsb[0], hsb[1], hsb[2], MathHelper.clamp_int((int)(alpha * 255.0F), 0, 0xFF));
    }

    public int getRGB() {
        return this.get().getRGB();
    }

    public int getRed() {
        return this.get().getRed();
    }

    public int getGreen() {
        return this.get().getGreen();
    }

    public int getBlue() {
        return this.get().getBlue();
    }

    public int getAlpha() {
        return this.get().getAlpha();
    }

    public float[] getHSB() {
        return Color.RGBtoHSB(this.get().getRed(), this.get().getGreen(), this.get().getBlue(), null);
    }

    @Override
    public String getDisplayValue() {
        return String.format("#%08X", this.getRGB());
    }

    @Override
    public void parseString(String str) {
        this.set(Color.decode(str));
    }

    public String getDisplayValueNoAlpha() {
        return String.format("#%06X", this.getRGB() & 0xFFFFFF);
    }

    @Override
    public JsonElement serialize() {
        JsonObject colorObject = new JsonObject();
        colorObject.addProperty("red", this.getRed());
        colorObject.addProperty("green", this.getGreen());
        colorObject.addProperty("blue", this.getBlue());
        colorObject.addProperty("alpha", this.getAlpha());
        return colorObject;
    }

    @Override
    public void deserialize(JsonElement element) {
        try {
            if (element instanceof JsonObject object) {
                int red = object.has("red") ? object.get("red").getAsInt() : 0;
                int green = object.has("green") ? object.get("green").getAsInt() : 0;
                int blue = object.has("blue") ? object.get("blue").getAsInt() : 0;
                int alpha = object.has("alpha") ? object.get("alpha").getAsInt() : 0;
                this.set(new Color(red, green, blue, alpha));
            }
        } catch (Exception ignored) {}
    }

}
