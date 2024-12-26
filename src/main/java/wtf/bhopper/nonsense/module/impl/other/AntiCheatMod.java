package wtf.bhopper.nonsense.module.impl.other;

import wtf.bhopper.nonsense.anticheat.check.Check;
import wtf.bhopper.nonsense.anticheat.check.CheckInfo;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ModuleInfo(name = "Anti Cheat",
        description = "Detects other cheaters",
        category = ModuleCategory.OTHER,
        hidden = true,
        toggled = true,
        searchAlias = {"Hacker Detector", "Cheater Detector"})
public class AntiCheatMod extends Module {

    private final GroupProperty checksGroup = new GroupProperty("Checks", "Anti Cheat checks", this);
    private final Map<Check, BooleanProperty> checkProperties = new ConcurrentHashMap<>();

    public AntiCheatMod() {
        this.addProperties(this.checksGroup);
    }

    public void addCheckProperty(Check check) {
        BooleanProperty property = new BooleanProperty(check.name, check.description, check.getClass().getAnnotation(CheckInfo.class).enabled());
        this.checkProperties.put(check, property);
        this.checksGroup.addProperties(property);
    }

    public boolean checkEnabled(Check check) {
        return this.checkProperties.get(check).get();
    }

}
