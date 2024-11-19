package wtf.bhopper.nonsense.module.impl.movement;

import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventStrafe;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.util.minecraft.RotationUtil;

@ModuleInfo(name = "Movement Fix",
        description = "Updates your movement based on your server rotations.",
        category = ModuleCategory.MOVEMENT)
public class MovementFix extends Module {

    @EventLink
    public final Listener<EventStrafe> onStrafe = event -> event.yaw = RotationUtil.serverYaw;

}
