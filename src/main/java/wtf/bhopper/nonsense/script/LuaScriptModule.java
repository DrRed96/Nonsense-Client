package wtf.bhopper.nonsense.script;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;

@ModuleInfo(name = "SCRIPT_MODULE", description = "SCRIPT_MODULE", category = ModuleCategory.SCRIPT)
public class LuaScriptModule extends Module {

    private LuaTable data;

    public LuaScriptModule(String name, String description, LuaTable data) {
        this.data = data;
        try {
            this.getClass().getDeclaredField("name").set(this, name);
            this.getClass().getDeclaredField("description").set(this, description);

        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void onEnable() {
        ScriptManager.runScriptLua(() -> {
            if (this.data.get("on_enable").isfunction()) {
                this.data.get("on_enable").checkfunction().call();
            }
        });
    }

    @Override
    public void onDisable() {
        ScriptManager.runScriptLua(() -> {
            if (this.data.get("on_disable").isfunction()) {
                this.data.get("on_disable").checkfunction().call();
            }
        });
    }

    @EventLink
    public final Listener<EventPreMotion> onPreMotion = event -> {
        ScriptManager.runScriptLua(() -> {
            if (this.data.get("on_pre_motion").isfunction()) {
                LuaTable ctx = new LuaTable();
                ctx.set("x", event.x);
                ctx.set("y", event.y);
                ctx.set("z", event.z);
                ctx.set("yaw", event.yaw);
                ctx.set("pitch", event.pitch);
                ctx.set("on_ground", event.onGround ? LuaValue.TRUE : LuaValue.FALSE);
                LuaValue result = this.data.get("on_pre_motion").checkfunction().call(ctx);
                if (result.istable()) {
                    event.x = result.checktable().get("x").checkdouble();
                    event.y = result.checktable().get("y").checkdouble();
                    event.z = result.checktable().get("z").checkdouble();
                    event.yaw = (float)result.checktable().get("yaw").checkdouble();
                    event.pitch = (float)result.checktable().get("pitch").checkdouble();
                    event.onGround = result.checktable().get("on_ground").checkboolean();
                }
            }
        });
    };

}
