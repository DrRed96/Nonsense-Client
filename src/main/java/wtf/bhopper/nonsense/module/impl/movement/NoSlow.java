package wtf.bhopper.nonsense.module.impl.movement;

import wtf.bhopper.nonsense.event.Cancellable;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventSlowDown;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;

@ModuleInfo(name = "No Slow",
        description = "Prevents you from being slowed down while using an item.",
        category = ModuleCategory.MOVEMENT)
public class NoSlow extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Method for no slow.", Mode.VANILLA);
    private final BooleanProperty sprintReset = new BooleanProperty("Sprint Reset", "Prevents sprint from being reset.", true);

    public NoSlow() {
        this.addProperties(this.mode, this.sprintReset);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @EventLink
    public final Listener<EventSlowDown> onSlowDown = Cancellable::cancel;

    public boolean usingItemFixed() {
        if (this.isToggled() && this.sprintReset.get()) {
            return false;
        }

        return mc.thePlayer.isUsingItem();
    }

    private enum Mode {
        VANILLA
    }

}
