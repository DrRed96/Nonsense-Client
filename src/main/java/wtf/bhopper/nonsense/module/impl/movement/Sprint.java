package wtf.bhopper.nonsense.module.impl.movement;

import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventPostMotion;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;

@ModuleInfo(name = "Sprint",
        description = "Makes you sprint.",
        category = ModuleCategory.MOVEMENT)
public class Sprint extends Module {

    private final BooleanProperty keepSprint = new BooleanProperty("Keep Sprint", "Prevents sprint from being reset while attacking", false);

    public Sprint() {
        this.addProperties(this.keepSprint);
    }

    @EventLink
    public final Listener<EventPostMotion> onPost = event -> mc.thePlayer.setSprinting(MoveUtil.canSprint(false));
    
    public boolean keepSprint() {
        return this.isToggled() && this.keepSprint.get();
    }

}
