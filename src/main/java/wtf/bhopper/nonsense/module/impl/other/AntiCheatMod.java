package wtf.bhopper.nonsense.module.impl.other;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.anticheat.check.AbstractCheck;
import wtf.bhopper.nonsense.anticheat.check.CheckInfo;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.ButtonProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;

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
    private final Map<AbstractCheck, BooleanProperty> checkProperties = new ConcurrentHashMap<>();
    private final ButtonProperty resetPlayerData = new ButtonProperty("Reset Data", "Reset Anti-Cheat player data", () -> Nonsense.getAntiCheat().clearPlayerData());

    public AntiCheatMod() {
        super();
        this.addProperties(this.checksGroup, this.resetPlayerData);
    }

    public void addCheckProperty(AbstractCheck check) {
        BooleanProperty property = new BooleanProperty(check.name, check.description, check.getClass().getAnnotation(CheckInfo.class).enabled());
        this.checkProperties.put(check, property);
        this.checksGroup.addProperties(property);
    }

    public boolean checkEnabled(AbstractCheck check) {
        return this.checkProperties.get(check).get();
    }

}
