package wtf.bhopper.nonsense.script;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.script.java.JavaEnvironment;
import wtf.bhopper.nonsense.script.lua.LuaEnvironment;
import wtf.bhopper.nonsense.script.lua.LuaScript;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScriptManager {

    private final File scriptDir;

    private final JavaEnvironment javaEnvironment;
    private final LuaEnvironment luaEnvironment;

    private final List<LuaScript> luaScripts = new ArrayList<>();

    public ScriptManager() {
        this.scriptDir = new File(Nonsense.getDataDir(), "scripts");
        this.scriptDir.mkdirs();

        this.javaEnvironment = new JavaEnvironment();
        this.luaEnvironment = new LuaEnvironment();

        this.loadScripts();
    }

    public void loadScripts(boolean notify) {
        try {
            this.loadLuaScripts();
        } catch (IOException exception) {
            ChatUtil.error("Failed to load lua scripts: %s", exception.getMessage());
            if (notify) {
                Notification.send("Lua Scripts", "Failed to load lua scripts: " + exception.getMessage(), NotificationType.ERROR, 5000);
            }
        }

        Nonsense.getModuleManager().clearScriptModules();

        for (LuaScript script : this.luaScripts) {
            Nonsense.LOGGER.info("Run script: {}", script.getFile());
            this.luaEnvironment.runScript(script);
        }

        if (notify) {
            Notification.send("Script Manager", "Reloaded scripts.", NotificationType.SUCCESS, 3000);
        }

    }

    public void loadScripts() {
        this.loadScripts(false);
    }

    private void loadLuaScripts() throws IOException {
        this.luaScripts.clear();

        File[] files = this.scriptDir.listFiles();
        if (files == null) {
            throw new IOException("Failed to list files.");
        }

        this.luaScripts.addAll(Arrays.stream(files)
                .filter(file -> !file.isDirectory() && file.getName().endsWith(".lua"))
                .map(LuaScript::new)
                .toList());
    }

    public File getScriptDir() {
        return this.scriptDir;
    }

    public JavaEnvironment getJavaEnv() {
        return this.javaEnvironment;
    }

    public LuaEnvironment getLuaEnv() {
        return this.luaEnvironment;
    }

}
