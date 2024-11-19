package wtf.bhopper.nonsense.gui.hud;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationManager;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;

public class Hud {

    public final ModuleList moduleList = new ModuleList();
    public final NotificationManager notifications = new NotificationManager();

    public Hud() {
        this.moduleList.init();
    }

    public static HudMod mod() {
        return Nonsense.module(HudMod.class);
    }

    public static boolean enabled() {
        return mod().isToggled();
    }

}
