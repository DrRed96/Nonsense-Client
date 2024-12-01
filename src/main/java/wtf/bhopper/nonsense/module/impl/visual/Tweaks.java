package wtf.bhopper.nonsense.module.impl.visual;

import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;

@ModuleInfo(name = "Tweaks", description = "Visuals tweaks/improvements.", category = ModuleCategory.VISUAL)
public class Tweaks extends Module {

    private final BooleanProperty viewClip = new BooleanProperty("View Clip", "Prevents your FOV from changing in 3rd person.", true);
    private final BooleanProperty minimalBobbing = new BooleanProperty("Minimal Bobbing", "Prevents the world from shaking when view bobbing is turned on.", false);

    public Tweaks() {
        this.addProperties(this.viewClip, this.minimalBobbing);
    }

    public boolean viewClip() {
        return this.isToggled() && this.viewClip.get();
    }

    public boolean minimalBobbing() {
        return this.isToggled() && this.minimalBobbing.get();
    }

}
