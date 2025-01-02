package wtf.bhopper.nonsense.gui.hud.notification;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.Display;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;
import wtf.bhopper.nonsense.util.minecraft.IMinecraft;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager implements IMinecraft {

    private final List<Notification> notifications = new CopyOnWriteArrayList<>();

    public void addNotification(Notification notification) {
        if (!Hud.enabled() || !Hud.mod().notificationEnabled.get())  {
            return;
        }

        this.notifications.addFirst(notification);

        try {
            if (!Hud.mod().notificationSound.is(HudMod.NotificationSound.NONE)) {
                mc.getSoundHandler().playSound(Hud.mod().notificationSound.get().createSoundRecord());
            }
        } catch (Exception ignored) {}
    }

    public void draw(ScaledResolution scaledRes, float delta) {
        if (!Hud.enabled() || !Hud.mod().notificationEnabled.get()) {
            return;
        }

        this.notifications.removeIf(Notification::isDone);

        GlStateManager.pushMatrix();
        scaledRes.scaleToFactor(1.0F);

        int offset = Display.getHeight() - 72;
        int right = Display.getWidth();
        for (Notification notification : this.notifications) {
            offset = notification.draw(delta, offset, right);
        }
        GlStateManager.popMatrix();
    }

}
