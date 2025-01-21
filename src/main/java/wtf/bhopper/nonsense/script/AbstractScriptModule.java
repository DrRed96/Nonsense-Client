package wtf.bhopper.nonsense.script;

import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;

public abstract class AbstractScriptModule extends Module {

    public AbstractScriptModule(String name, String description) {
        super(name, description, ModuleCategory.SCRIPT);
    }

}
