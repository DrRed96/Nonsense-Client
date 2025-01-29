package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.entity.Entity;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;

@ModuleInfo(name = "No Render",
        description = "Prevents certain things from being rendered",
        category = ModuleCategory.VISUAL)
public class NoRender extends AbstractModule {

    public final BooleanProperty hurtCamera = new BooleanProperty("Hurt Camera", "Removes the shake effect from the camera when taking damage", true);
    public final BooleanProperty blindness = new BooleanProperty("Blindness", "Prevents the blindness effect", true);
    public final BooleanProperty nausea = new BooleanProperty("Nausea", "Prevents the nausea effect", true);
    public final BooleanProperty pumpkinOverlay = new BooleanProperty("Pumpkin Overlay", "Prevents the pumpkin overlay from rendering.", true);
    public final BooleanProperty enchantTable = new BooleanProperty("Enchantment Table", "Prevents the books on top of enchantment tables from rendering", false);

    public NoRender() {
        super();
        this.addProperties(this.hurtCamera, this.blindness, this.nausea, this.pumpkinOverlay, this.enchantTable);
    }

    public boolean blindness(Entity entity) {
        return this.isToggled() && this.blindness.get() && entity.isClientPlayer();
    }

    public boolean nausea() {
        return this.isToggled() && this.nausea.get();
    }

    public boolean pumpkin() {
        return this.isToggled() && this.pumpkinOverlay.get();
    }

}
