package wtf.bhopper.nonsense.module.impl.movement;

import wtf.bhopper.nonsense.event.EventPriorities;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.movement.EventStrafe;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;

@ModuleInfo(name = "Strafe",
        description = "Makes your motion smoother",
        category = ModuleCategory.MOVEMENT)
public class Strafe extends AbstractModule {

    @EventLink(EventPriorities.HIGH)
    public final Listener<EventStrafe> onStrafe = _ -> MoveUtil.setSpeed(MoveUtil.getSpeed());

}
