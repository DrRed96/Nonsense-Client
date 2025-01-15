package wtf.bhopper.nonsense.module.impl.combat;

import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;

@ModuleInfo(name = "No Click Delay",
        description = "Removes the click delay after missing an attack",
        category = ModuleCategory.COMBAT)
public class NoClickDelay extends Module {

    @EventLink
    public final Listener<EventTick> onTick = _ -> {
        if (PlayerUtil.canUpdate()) {
            mc.leftClickCounter = 0;
        }
    };

}
