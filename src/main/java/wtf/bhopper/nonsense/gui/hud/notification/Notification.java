package wtf.bhopper.nonsense.gui.hud.notification;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;
import wtf.bhopper.nonsense.util.minecraft.IMinecraft;
import wtf.bhopper.nonsense.util.misc.Stopwatch;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.NVGHelper;
import wtf.bhopper.nonsense.util.render.RenderUtil;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;
import static org.lwjgl.opengl.GL11.*;

public class Notification implements IMinecraft {

    private static final float OFFSET = 30.0F;
    private static final float HEIGHT = 48.0F;
    private static final float BAR_HEIGHT = 4.0F;
    private static final float FADE_FACTOR = 0.25F;

    private static final int TITLE_COLOR = 0xFFFFFFFF;
    private static final int MSG_COLOR = 0xFFAAAAAA;
    private static final int BG_COLOR = 0x80000000;
    private static final int OUTLINE_COLOR = 0xFF111111;

    private final String title;
    private final String message;
    private final NotificationType type;
    private final int displayTimeMS;

    private int stage = 0;
    private float positionFactor = 0.0F;
    private final Stopwatch clock = new Stopwatch();

    public Notification(String title, String message, NotificationType type, int displayTimeMS) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.displayTimeMS = displayTimeMS;
        this.clock.reset();
    }

    public int draw(float delta, int offset, int right) {

        HudMod mod = Hud.mod();

        NVGHelper.begin();
        NVGHelper.fontFace(mod.font.get().font);
        NVGHelper.fontSize(18.0F);
        NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);

        Hud.WidthMethod widthMethod = mod.font.is(HudMod.Font.MINECRAFT)
                ? text -> mc.fontRendererObj.getStringWidthF(text) * 2.0F
                : NVGHelper::getStringWidth;

        float width = Math.max(widthMethod.getWidth(this.title), widthMethod.getWidth(message)) + HEIGHT + 8.0F;

        switch (this.stage) {
            case 0 -> {
                this.positionFactor += FADE_FACTOR * delta;
                if (this.positionFactor >= 1.0F) {
                    this.positionFactor = 1.0F;
                    this.stage = 1;
                }
            }

            case 1 -> {
                if (this.clock.hasReached(this.displayTimeMS)) {
                    this.stage = 2;
                }
            }

            case 2 -> {
                this.positionFactor -= FADE_FACTOR * delta;
                if (this.positionFactor <= 0.0F) {
                    this.positionFactor = 0.0F;
                    this.stage = 3;
                }
            }

            default -> {
                return offset;
            }
        }

        float x = right - width * positionFactor - OFFSET;
        float y = offset - HEIGHT;

        // Background
        NVGHelper.drawRect(x, y, width, HEIGHT, BG_COLOR);

        // NVG Text
        if (!mod.font.is(HudMod.Font.MINECRAFT)) {
            NVGHelper.drawText(this.title, x + 48.0F, y + 14.0F, TITLE_COLOR, true);
            NVGHelper.drawText(this.message, x + 48.0F, y + 30.0F, MSG_COLOR, true);
        }

        // Bar
        NVGHelper.drawRect(x, y + HEIGHT - BAR_HEIGHT, width, BAR_HEIGHT, ColorUtil.darken(this.type.color.getRGB(), 2));
        NVGHelper.drawRect(x, y + HEIGHT - BAR_HEIGHT, width * this.doneFactor(), BAR_HEIGHT, this.type.color.getRGB());

        // Outline
        NVGHelper.drawRect(x - 1.0F, y - 1.0F, width + 2.0F, 1.0F, OUTLINE_COLOR);
        NVGHelper.drawRect(x - 1.0F, y + HEIGHT, width + 2.0F, 1.0F, OUTLINE_COLOR);
        NVGHelper.drawRect(x - 1.0F, y - 1.0F, 1.0F, HEIGHT + 2.0F, OUTLINE_COLOR);
        NVGHelper.drawRect(x + width, y - 1.0F, 1.0F, HEIGHT + 2.0F, OUTLINE_COLOR);

        NVGHelper.end();

        if (mod.font.is(HudMod.Font.MINECRAFT)) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            mc.fontRendererObj.drawStringWithShadow(this.title, x / 2.0F + 24.0F, y / 2.0F + 2.5F, TITLE_COLOR);
            mc.fontRendererObj.drawStringWithShadow(this.message, x / 2.0F + 24.0F, y / 2.0F + 10.5F, MSG_COLOR);
            GlStateManager.popMatrix();
        }

        // Rendering images with NanoVG is aids, so we're just doing it with OpenGL
        mc.getTextureManager().bindTexture(this.type.icon);
        glPushAttrib(GL_COLOR_BUFFER_BIT);
        RenderUtil.glColor(ColorUtil.WHITE);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        Gui.drawModalRectWithCustomSizedTexture((int)x + 8, (int)y + 6, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        GlStateManager.disableBlend();
        glPopAttrib();

        return offset - (int)((HEIGHT + 4) * this.positionFactor);
    }

    public boolean isDone() {
        return stage == 3;
    }

    private float doneFactor() {
        return Math.min((float)this.clock.passedTime() / (float)this.displayTimeMS, 1.0F);
    }

    public static void send(String title, String message, NotificationType type, int displayTimeMS) {
        Nonsense.getHud().notifications.addNotification(new Notification(title, message, type, displayTimeMS));
    }

}
