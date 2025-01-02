package wtf.bhopper.nonsense.module.impl.visual;

import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.render.EventRenderNameTag;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;

@ModuleInfo(name = "Tweaks", description = "Visuals tweaks/improvements.", category = ModuleCategory.VISUAL)
public class Tweaks extends Module {

    private final BooleanProperty viewClip = new BooleanProperty("View Clip", "Prevents your FOV from changing in 3rd person.", true);
    private final BooleanProperty minimalBobbing = new BooleanProperty("Minimal Bobbing", "Prevents the world from shaking when view bobbing is turned on.", false);
    private final BooleanProperty nameTagShadow = new BooleanProperty("Name Tag Shadow", "Draws text with a drop shadow in name tags", true);

    public Tweaks() {
        this.autoAddProperties();
    }

    @EventLink
    public final Listener<EventRenderNameTag> onRenderNameTag = event -> {
        if (this.nameTagShadow.get()) {
            event.shadow = true;
        }
    };

    public boolean viewClip() {
        return this.isToggled() && this.viewClip.get();
    }

    public boolean minimalBobbing() {
        return this.isToggled() && this.minimalBobbing.get();
    }

}
