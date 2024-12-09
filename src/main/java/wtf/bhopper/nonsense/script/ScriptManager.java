package wtf.bhopper.nonsense.script;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.lib.jse.JsePlatform;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.script.api.lua.ClientLuaApi;
import wtf.bhopper.nonsense.script.api.lua.PlayerLuaApi;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;

import java.io.File;

public class ScriptManager {

    public static final Globals LUA = JsePlatform.standardGlobals();

    private final File scriptDir;

    public ScriptManager() {
        this.scriptDir = new File(Nonsense.getDataDir(), "scripts");
        if (!this.scriptDir.exists()) {
            if (!this.scriptDir.mkdirs()) {
                throw new RuntimeException("Failed to create scripts directory");
            }
        }
    }

    public void loadScript(Script script) {
        runScriptLua(() -> LUA.loadfile(script.file.getAbsolutePath()).call());
    }

    public static void runScriptLua(Runnable runnable) {
        try {
            runnable.run();
        } catch (LuaError luaError) {
            ChatUtil.error("Lua Error: %s", luaError.getMessage());
            Nonsense.LOGGER.error("Lua Error in script ", luaError);
        } catch (Throwable throwable) {
            ChatUtil.error("Java Error: %s", throwable.getMessage());
            Nonsense.LOGGER.error("Java Error in script", throwable);
        }
    }

    static {
        LUA.set("client", new ClientLuaApi());
        LUA.set("player", new PlayerLuaApi());
    }

}
