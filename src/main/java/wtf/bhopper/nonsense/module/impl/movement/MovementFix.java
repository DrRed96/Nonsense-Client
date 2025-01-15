package wtf.bhopper.nonsense.module.impl.movement;

import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;

@ModuleInfo(name = "Movement Fix",
        description = "Updates your movement based on your server rotations.",
        category = ModuleCategory.MOVEMENT)
public class MovementFix extends Module {

    public final BooleanProperty optimize = new BooleanProperty("Optimize", "Optimizes your movement to be closer to the client yaw", true);

    public MovementFix() {
        super();
        this.addProperties(this.optimize);
    }

}
