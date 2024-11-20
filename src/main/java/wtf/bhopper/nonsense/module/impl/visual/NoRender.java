package wtf.bhopper.nonsense.module.impl.visual;

import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;

@ModuleInfo(name = "No Render", description = "Prevents certain things from being rendered", category = ModuleCategory.VISUAL)
public class NoRender extends Module {

    public final BooleanProperty hurtCamera = new BooleanProperty("Hurt Camera", "Removes the shake effect from the camera when taking damage", true);

    public NoRender() {
        this.addProperties(this.hurtCamera);
    }

}
