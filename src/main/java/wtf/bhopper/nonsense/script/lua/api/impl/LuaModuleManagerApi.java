package wtf.bhopper.nonsense.script.lua.api.impl;

import org.luaj.vm2.LuaTable;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.script.AbstractScriptModule;
import wtf.bhopper.nonsense.script.lua.LuaScriptModule;
import wtf.bhopper.nonsense.script.lua.api.LuaApi;

public class LuaModuleManagerApi extends LuaApi {

    public LuaModuleManagerApi() {
        this.addFunc("register", args -> {
            String name = args.arg(1).checkjstring();
            String description = args.arg(2).checkjstring();
            LuaTable moduleTable = args.arg(3).checktable();
            Nonsense.getModuleManager().addScriptModule(new LuaScriptModule(name, description, moduleTable));
            return NIL;
        });

        this.addFunc("register_boolean", args -> {
            String moduleName = args.arg(1).checkjstring();
            String propertyName = args.arg(2).checkjstring();
            String propertyDescription = args.arg(3).checkjstring();
            boolean defaultValue = args.arg(4).checkboolean();

            AbstractScriptModule module = Nonsense.getModuleManager().getScript(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("'" + moduleName + "' is not a valid script module.");
            }
            module.addProperties(new BooleanProperty(propertyName, propertyDescription, defaultValue));
            return NIL;
        });

    }

}
