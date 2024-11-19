package wtf.bhopper.nonsense.module.impl.movement;

import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventPostMotion;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;

@ModuleInfo(name = "Sprint",
        description = "Makes you sprint.",
        category = ModuleCategory.MOVEMENT)
public class Sprint extends Module {

    @EventLink
    public final Listener<EventPostMotion> onPost = event -> mc.thePlayer.setSprinting(this.canSprint());

    private boolean canSprint() {
        return mc.thePlayer.moveForward > 0.8F &&
                !mc.thePlayer.isSneaking() &&
                mc.thePlayer.getFoodStats().getFoodLevel() >= 6 &&
                !mc.thePlayer.isCollidedHorizontally;
    }

}
