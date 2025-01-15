package wtf.bhopper.nonsense.module.impl.other;

import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;

@ModuleInfo(name = "Anti Desync",
        description = "Prevents block de-syncs",
        category = ModuleCategory.OTHER)
public class AntiDesync extends Module {

    private final BooleanProperty breakSet = new BooleanProperty("Break", "Block breaking", true);
    private final BooleanProperty placeSet = new BooleanProperty("Place", "Block placing", false);

    public AntiDesync() {
        super();
        this.addProperties(this.breakSet, this.placeSet);
    }

    public boolean breaking() {
        return this.isToggled() && this.breakSet.get();
    }

    public boolean placing() {
        return this.isToggled() && this.placeSet.get();
    }

}
