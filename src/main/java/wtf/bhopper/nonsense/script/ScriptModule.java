package wtf.bhopper.nonsense.script;

import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;

public abstract class ScriptModule extends Module {

    public ScriptModule(String name, String description) {
        super(name, description, ModuleCategory.SCRIPT);
    }

}
