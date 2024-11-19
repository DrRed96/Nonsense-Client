package wtf.bhopper.nonsense.gui.hud.notification;

import org.lwjglx.util.vector.Vector2f;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.misc.Clock;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.Fonts;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import static org.lwjgl.nanovg.NanoVG.*;

public class Notification implements MinecraftInstance {

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
    private final Clock clock = new Clock();

    public Notification(String title, String message, NotificationType type, int displayTimeMS) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.displayTimeMS = displayTimeMS;
        this.clock.reset();
    }

    public int draw(float delta, int offset, int right) {

        NVGHelper.fontFace(Fonts.ARIAL);
        NVGHelper.fontSize(18.0F);
        NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);


        float width = Math.max(NVGHelper.getStringWidth(this.title), NVGHelper.getStringWidth(message)) + HEIGHT + 8.0F;

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

        NVGHelper.drawRect(x, y, width, HEIGHT, BG_COLOR);
        NVGHelper.drawImage(x + 8.0F, y + 8.0F, 32.0F, 32.0F, 32.0F, 32.0F, this.type.getNvgImage());
        NVGHelper.drawText(this.title, x + 48.0F, y + 14.0F, TITLE_COLOR, true);
        NVGHelper.drawText(this.message, x + 48.0F, y + 30.0F, MSG_COLOR, true);
        NVGHelper.drawRect(x, y + HEIGHT - BAR_HEIGHT, width, BAR_HEIGHT, ColorUtil.darken(this.type.color.getRGB(), 2));
        NVGHelper.drawRect(x, y + HEIGHT - BAR_HEIGHT, width * Math.min((float)this.clock.getTime() / (float)this.displayTimeMS, 1.0F), BAR_HEIGHT, this.type.color.getRGB());

        NVGHelper.drawRect(x - 1.0F, y - 1.0F, width + 2.0F, 1.0F, OUTLINE_COLOR);
        NVGHelper.drawRect(x - 1.0F, y + HEIGHT, width + 2.0F, 1.0F, OUTLINE_COLOR);
        NVGHelper.drawRect(x - 1.0F, y - 1.0F, 1.0F, HEIGHT + 2.0F, OUTLINE_COLOR);
        NVGHelper.drawRect(x + width, y - 1.0F, 1.0F, HEIGHT + 2.0F, OUTLINE_COLOR);

        return offset - (int)((HEIGHT + 4) * this.positionFactor);
    }

    public boolean isDone() {
        return stage == 3;
    }

    private float doneFactor() {
        return 1.0F - Math.min((float)this.clock.getTime() / (float)this.displayTimeMS, 1.0F);
    }

    public static void send(String title, String message, NotificationType type, int displayTimeMS) {
        Nonsense.getHud().notifications.addNotification(new Notification(title, message, type, displayTimeMS));
    }

}
