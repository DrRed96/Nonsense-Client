package wtf.bhopper.nonsense.gui.hud.notification;

import org.lwjglx.opengl.Display;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager implements MinecraftInstance {

    private final List<Notification> notifications = new ArrayList<>();

    public NotificationManager() {
        NotificationType.createImages();
    }

    public void addNotification(Notification notification) {
        if (!Hud.enabled() || !Hud.mod().notificationEnabled.get())  {
            return;
        }

        this.notifications.add(0, notification);

        try {
            if (!Hud.mod().notificationSound.is(HudMod.NotificationSound.NONE)) {
                mc.getSoundHandler().playSound(Hud.mod().notificationSound.get().createSoundRecord());
            }
        } catch (Exception ignored) {}
    }

    public void draw(float delta) {
        if (!Hud.enabled() || !Hud.mod().notificationEnabled.get()) {
            return;
        }

        this.notifications.removeIf(Notification::isDone);

        NVGHelper.begin();
        int offset = Display.getHeight() - 72;
        int right = Display.getWidth();
        for (Notification notification : this.notifications) {
            offset = notification.draw(delta, offset, right);
        }
        NVGHelper.end();
    }

}
