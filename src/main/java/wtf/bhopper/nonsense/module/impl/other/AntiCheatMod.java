package wtf.bhopper.nonsense.module.impl.other;

import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;

@ModuleInfo(name = "Anti Cheat", description = "Detects other cheaters", category = ModuleCategory.OTHER, hidden = true, toggled = true)
public class AntiCheatMod extends Module {

    public final BooleanProperty unreliable = new BooleanProperty("Unreliable Checks", "Use checks that are likely to false flag depending on server conditions", true);

    public AntiCheatMod() {
        this.autoAddProperties();
    }

}
