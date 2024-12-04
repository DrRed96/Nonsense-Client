package wtf.bhopper.nonsense.gui.hud.notification;

import net.minecraft.util.ResourceLocation;
import org.lwjgl.nanovg.NanoVG;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import java.awt.*;

public enum NotificationType {
    INFO(0xAAAAAA, "nonsense/notification/info.png"),
    SUCCESS(0x55FF55, "nonsense/notification/success.png"),
    WARNING(0xFFFF55, "nonsense/notification/warning.png"),
    ERROR(0xFF5555, "nonsense/notification/error.png");

    public final Color color;
    public final ResourceLocation icon;
    private int nvgImage = -1;

    NotificationType(int color, String icon) {
        this.color = new Color(color | 0xFF000000);
        this.icon = new ResourceLocation(icon);
    }

    public float red() {
        return (float)color.getRed() / 255.0F;
    }

    public float green() {
        return (float)color.getGreen() / 255.0F;
    }

    public float blue() {
        return (float)color.getBlue() / 255.0F;
    }

    public int getNvgImage() {
        return this.nvgImage;
    }

}
