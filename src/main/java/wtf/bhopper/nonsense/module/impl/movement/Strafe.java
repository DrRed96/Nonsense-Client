package wtf.bhopper.nonsense.module.impl.movement;

import wtf.bhopper.nonsense.event.EventPriorities;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventMove;
import wtf.bhopper.nonsense.event.impl.EventStrafe;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.util.minecraft.MoveUtil;

@ModuleInfo(name = "Strafe", description = "Makes your motion smoother", category = ModuleCategory.MOVEMENT)
public class Strafe extends Module {

    @EventLink(EventPriorities.HIGH)
    public final Listener<EventStrafe> onStrafe = event -> event.friction = 1.0F;

}
