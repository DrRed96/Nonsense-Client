package wtf.bhopper.nonsense.module.impl.movement;

import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;

@ModuleInfo(name = "No Jump Delay",
        description = "Removes the delay between jumping",
        category = ModuleCategory.MOVEMENT)
public class NoJumpDelay extends Module {
    @EventLink
    public final Listener<EventTick> onTick = _ -> {
        if (mc.inGameHasFocus && PlayerUtil.canUpdate()) {
            mc.thePlayer.jumpTicks = 0;
        }
    };
}
