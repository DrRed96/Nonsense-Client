package wtf.bhopper.nonsense.script;

import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;

public abstract class AbstractScriptModule extends AbstractModule {

    public AbstractScriptModule(String name, String description) {
        super(name, description, ModuleCategory.SCRIPT);
    }

}
