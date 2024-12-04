package wtf.bhopper.nonsense.util.render;

import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjglx.opengl.Display;
import org.lwjglx.util.vector.Vector2f;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;

import java.awt.*;
import java.nio.ByteBuffer;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.opengl.GL11.*;

public class NVGHelper implements MinecraftInstance {

    private static long context;
    private static NVGColor color;
    private static NVGColor color2;
    private static NVGPaint paint;

    public static void init() {
        context = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);

        if (context == 0L) {
            throw new RuntimeException("Failed to initialize NanoVG");
        }

        color = NVGColor.create();
        color2 = NVGColor.create();
        paint = NVGPaint.create();
    }

    public static long ctx() {
        return context;
    }

    public static void begin() {
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        nvgBeginFrame(context, Display.getWidth(), Display.getHeight(), 1.0F);
    }

    public static void end() {
        nvgEndFrame(context);

        glPopAttrib();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void beginPath() {
        nvgBeginPath(context);
    }

    public static void closePath() {
        nvgClosePath(context);
    }

    public static void fill() {
        nvgFill(context);
    }

    public static void fillColor(int color) {
        nvgFillColor(context, getColor(color));
    }

    public static void fillPaint(NVGPaint paint) {
        nvgFillPaint(context, paint);
    }

    public static void stroke() {
        nvgStroke(context);
    }

    public static void strokeColor(int color) {
        nvgStrokeColor(context, getColor(color));
    }

    public static void strokePaint(NVGPaint paint) {
        nvgStrokePaint(context, paint);
    }

    public static void strokeWidth(float size) {
        nvgStrokeWidth(context, size);
    }

    public static void moveTo(float x, float y) {
        nvgMoveTo(context, x, y);
    }

    public static void lineTo(float x, float y) {
        nvgLineTo(context, x, y);
    }

    public static void rect(float x, float y, float w, float h) {
        nvgRect(context, x, y, w, h);
    }

    public static void roundedRect(float x, float y, float w, float h, float r) {
        nvgRoundedRect(context, x, y, w, h, r);
    }

    public static void circle(float cx, float cy, float r) {
        nvgCircle(context, cx, cy, r);
    }

    public static void fontFace(Fonts font) {
        nvgFontFaceId(context, font.getVgFont());
    }

    public static void fontSize(float size) {
        nvgFontSize(context, size);
    }

    public static void text(float x, float y, String string) {
        nvgText(context, x, y, string);
    }

    public static void textAlign(int align) {
        nvgTextAlign(context, align);
    }

    public static float textBounds(float x, float y, String string, float[] bounds) {
        return nvgTextBounds(context, x, y, string, bounds);
    }

    public static float textBounds(float x, float y, String string) {
        return nvgTextBounds(context, x, y, string, new float[4]);
    }

    public static NVGPaint linearGradient(float sx, float sy, float ex, float ey, int icol, int ocol) {
        return nvgLinearGradient(context, sx, sy, ex, ey, getColor(icol), getColor(ocol, color2), paint);
    }

    public static NVGPaint linearGradient(float sx, float sy, float ex, float ey, Color icol, Color ocol) {
        return nvgLinearGradient(context, sx, sy, ex, ey, getColor(icol), getColor(ocol, color2), paint);
    }

    public static int createImage(int flags, ByteBuffer data) {
        return nvgCreateImageMem(context, flags, data);
    }

    public static int createFontMem(String name, ByteBuffer data, boolean freeData) {
        return nvgCreateFontMem(context, name, data, freeData);
    }

    public static void translate(float x, float y) {
        nvgTranslate(context, x, y);
    }

    public static void resetTransform() {
        nvgResetTransform(context);
    }

    public static void scissor(float x, float y, float w, float h) {
        nvgScissor(context, x, y, w, h);
    }

    public static void resetScissor() {
        nvgResetScissor(context);
    }

    public static void reset() {
        nvgReset(context);
    }

    public static NVGPaint imagePattern(float ox, float oy, float ex, float ey, float angle, int image, float alpha) {
        return nvgImagePattern(context, ox, oy, ex, ey, angle, image, alpha, paint);
    }

    public static void drawLine(float x1, float y1, float x2, float y2, float width, int color) {
        beginPath();
        moveTo(x1, y1);
        lineTo(x2, y2);
        strokeColor(color);
        strokeWidth(width);
        stroke();
        closePath();
    }

    public static void drawLine(float width, int color, Vector2f... points) {
        boolean firstPoint = true;
        beginPath();
        for (Vector2f point : points) {
            if (firstPoint) {
                moveTo(point.x, point.y);
                firstPoint = false;
            } else {
                lineTo(point.x, point.y);
            }
        }
        strokeColor(color);
        strokeWidth(width);
        stroke();
        closePath();
    }

    public static void drawRect(float x, float y, float width, float height, int color) {
        beginPath();
        rect(x, y, width, height);
        fillColor(color);
        fill();
        closePath();
    }

    public static void drawRectOutline(float x, float y, float width, float height, int color) {
        drawRect(x + 1.0F, y, width - 1.0F, 1.0F, color);
        drawRect(x + 1.0F, y + height, width - 1.0F, 1.0F, color);
        drawRect(x, y, 1.0F, height + 1.0F, color);
        drawRect(x + width, y, 1.0F, height + 1.0F, color);
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        beginPath();
        roundedRect(x, y, width, height, radius);
        fillColor(color);
        fill();
        closePath();
    }

    public static void drawCircle(float x, float y, float radius, int color) {
        beginPath();
        circle(x, y, radius);
        fillColor(color);
        fill();
        closePath();
    }

    public static void drawOutlineCircle(float x, float y, float radius, float width, int color) {
        beginPath();
        circle(x, y, radius);
        strokeColor(color);
        strokeWidth(width);
        stroke();
        closePath();
    }

    public static void drawText(String text, float x, float y, int color, boolean shadow) {
        beginPath();
        if (shadow) {
            fillColor(ColorUtil.dropShadowColor(color));
            text(x + 1.0F, y + 1.0F, text);
        }
        fillColor(color);
        text(x, y, text);
        closePath();
    }

    public static void drawText(String text, float x, float y, int color) {
        drawText(text, x, y, color, false);
    }

    public static float getStringWidth(String text) {
        return textBounds(0.0F, 0.0F, text);
    }

    public static void drawGraidentRect(float x, float y, float width, float height, int startColor, int endColor) {
        beginPath();
        rect(x, y, width, height);
        fillPaint(linearGradient(x, y, x, y + height, startColor, endColor));
        fill();
        closePath();
    }

    public static void drawGraidentRectSideways(float x, float y, float width, float height, int startColor, int endColor) {
        beginPath();
        rect(x, y, width, height);
        fillPaint(linearGradient(x, y, x + width, y, startColor, endColor));
        fill();
        closePath();
    }

    public static void drawImage(float x, float y, float width, float height, float imageWidth, float imageHeight, int image) {
        beginPath();
        rect(x, y, width, height);
        fillPaint(imagePattern(0.0F, 0.0F, imageWidth, imageHeight, 0.0F, image, 1.0F));
        fill();
        closePath();
    }

    public static void drawColorPicker(float x, float y, float width, float height, float hue) {
        for (float yOff = y; yOff < y + height; yOff += 1.0F) {
            float b = 1.0F - (yOff - y) / height;
            beginPath();
            rect(x, yOff, width, 1.0F);
            fillPaint(linearGradient(x, yOff, x + width, yOff, Color.getHSBColor(hue, 0.0F, b), Color.getHSBColor(hue, 1.0F, b)));
            fill();
            closePath();
        }
    }

    public static void drawHueBar(float x, float y, float width, float height) {
        for (float yOff = y; yOff < y + height; yOff += 1.0F) {
            float hue = (yOff - y) / height;
            beginPath();
            rect(x, yOff, width, 1.0F);
            fillColor(Color.HSBtoRGB(hue, 1.0F, 1.0F));
            fill();
            closePath();
        }
    }

    public static int createImageFromResourceLocation(ResourceLocation location, int width, int height, int flags) {
        ITextureObject texture = mc.getTextureManager().getTexture(location);
        return nvglCreateImageFromHandle(context, texture.getGlTextureId(), width, height, flags);
    }

    public static NVGColor getColor(int color, NVGColor result) {
        byte red = (byte)((color >> 16) & 0xFF);
        byte green = (byte)((color >> 8) & 0xFF);
        byte blue = (byte)(color & 0xFF);
        byte alpha = (byte)((color >> 24) & 0xFF);

        return nvgRGBA(red, green, blue, alpha, result);
    }

    public static NVGColor getColor(Color color, NVGColor result) {
        return getColor(color.getRGB(), result);
    }

    public static NVGColor getColor(int color) {
        return getColor(color, NVGHelper.color);
    }

    public static NVGColor getColor(Color color) {
        return getColor(color, NVGHelper.color);
    }

}
