package wtf.bhopper.nonsense.script.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import wtf.bhopper.nonsense.script.lua.api.impl.LuaClientApi;
import wtf.bhopper.nonsense.script.lua.api.impl.LuaModuleManagerApi;
import wtf.bhopper.nonsense.script.lua.api.impl.LuaPlayerApi;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

public class LuaEnvironment {

    private final Globals globals = JsePlatform.standardGlobals();

    public LuaEnvironment() {
        this.globals.set("player", new LuaPlayerApi());
        this.globals.set("client", new LuaClientApi());
        this.globals.set("module_manager", new LuaModuleManagerApi());
    }

    public LuaValue runScript(LuaScript script) {
        try {
            LuaValue chunk = this.globals.loadfile(script.getFile().getAbsolutePath());
            return this.runScript(chunk);
        } catch (Exception exception) {
            ChatUtil.error("Failed to run script: %s", exception.getMessage());
        }

        return LuaValue.NIL;
    }

    public LuaValue runScript(LuaValue chunk) {
        try {
            return chunk.call();
        } catch (LuaError error) {
            ChatUtil.error("Lua Error: %s",  error.getMessage().replace('\n', ' '));
        } catch (Exception error) {
            ChatUtil.error("Lua VM Error: %s", error.getMessage().replace('\n', ' '));
        }

        return LuaValue.NIL;
    }

    public LuaValue runScript(LuaValue chunk, LuaValue arg1) {
        try {
            return chunk.call(arg1);
        } catch (LuaError error) {
            ChatUtil.error("Lua Error: %s",  error.getMessage().replace('\n', ' '));
        } catch (Exception error) {
            ChatUtil.error("Lua VM Error: %s", error.getMessage().replace('\n', ' '));
        }

        return LuaValue.NIL;
    }

    public Globals getGlobals() {
        return this.globals;
    }

}
