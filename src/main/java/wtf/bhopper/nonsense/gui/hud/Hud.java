package wtf.bhopper.nonsense.gui.hud;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationManager;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;

public class Hud implements MinecraftInstance {

    public final ModuleList moduleList = new ModuleList();
    public final InfoDisplay infoDisplay = new InfoDisplay();
    public final NotificationManager notifications = new NotificationManager();

    public Hud() {
        this.moduleList.init();
    }

    public static HudMod mod() {
        return Nonsense.module(HudMod.class);
    }

    public static boolean enabled() {
        return mod().isToggled() && (!mod().hideInF3.get() || !mc.gameSettings.showDebugInfo);
    }

}
