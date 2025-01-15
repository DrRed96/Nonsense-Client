package wtf.bhopper.nonsense.module.impl.player;

import net.minecraft.client.gui.GuiGameOver;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;

@ModuleInfo(name = "Auto Respawn",
        description = "Automatically respawns you upon death.",
        category = ModuleCategory.PLAYER)
public class AutoRespawn extends Module {

    @EventLink
    public final Listener<EventTick> onTick = _ -> {
        if (PlayerUtil.canUpdate() && mc.currentScreen instanceof GuiGameOver) {
            mc.thePlayer.respawnPlayer();
            mc.displayGuiScreen(null);
        }
    };

}
