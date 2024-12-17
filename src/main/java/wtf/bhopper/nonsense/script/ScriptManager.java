package wtf.bhopper.nonsense.script;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.lib.jse.JsePlatform;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.script.api.lua.ClientLuaApi;
import wtf.bhopper.nonsense.script.api.lua.PlayerLuaApi;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;
import wtf.bhopper.nonsense.util.misc.CaughtRunnable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

public class ScriptManager {

    public static final Globals LUA = JsePlatform.standardGlobals();
    public static final ScriptEngine JAVASCRIPT = new ScriptEngineManager().getEngineByName("nashorn");

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
        String name = script.file.getName();
        if (name.endsWith(".lua")) {
            runScriptLua(() -> LUA.loadfile(script.file.getAbsolutePath()).call());
        } else if (name.endsWith(".js")) {
            runScriptJs(() -> {
                try (Reader reader = new FileReader(script.file)) {
                    JAVASCRIPT.eval(reader);
                }
            });
        }
    }

    public static void runScriptLua(Runnable runnable) {
        try {
            runnable.run();
        } catch (LuaError luaError) {
            ChatUtil.error("Lua Error: %s", luaError.getMessage());
            Nonsense.LOGGER.error("Lua Error in script ", luaError);
        } catch (Throwable throwable) {
            ChatUtil.error("Java Error: %s", throwable.getMessage());
            Nonsense.LOGGER.error("Java Error in Lua script", throwable);
        }
    }

    public static void runScriptJs(CaughtRunnable runnable) {
        try {
            runnable.run();
        } catch (ScriptException exception) {
            ChatUtil.error("Script Exception (%d:%d): %s", exception.getLineNumber(), exception.getColumnNumber(), exception.getMessage());
            Nonsense.LOGGER.error("Script Exception", exception);
        } catch (Throwable throwable) {
            ChatUtil.error("Java Error: %s", throwable.getMessage());
            Nonsense.LOGGER.error("Java Error in JS script", throwable);
        }
    }

    static {
        LUA.set("client", new ClientLuaApi());
        LUA.set("player", new PlayerLuaApi());
    }

}
