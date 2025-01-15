package wtf.bhopper.nonsense.script;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.ButtonProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

import java.awt.*;
import java.io.IOException;

@ModuleInfo(name = "Script Options",
        description = "Configure script options.",
        category = ModuleCategory.SCRIPT)
public class ScriptOptionsMod extends Module {

    private final GroupProperty javaOptions = new GroupProperty("Java", "Configure Java scripts.", this);
    public final BooleanProperty javaUnsafe = new BooleanProperty("Allow Unsafe", "Allow potentially unsafe scripts to be ran.", false);
    public final BooleanProperty javaGameAccess = new BooleanProperty("Game Access", "Give scripts access to the Minecraft.getMinecraft() object.", false);

    private final GroupProperty luaOptions = new GroupProperty("Lua", "Configure lua scripts.", this);
    public final BooleanProperty luaHttp = new BooleanProperty("Allow HTTP", "Allow Lua scripts to make HTTP requests.", true);

    public final ButtonProperty reloadScripts = new ButtonProperty("Reload Scripts", "Click to reload scripts.", () -> Nonsense.getScriptManager().loadScripts(true));
    public final ButtonProperty openScriptsFolder = new ButtonProperty("Open Scripts Folder", "Click to open the scripts folder.", () -> {
        try {
            Desktop.getDesktop().open(Nonsense.getScriptManager().getScriptDir());
        } catch (IOException e) {
            ChatUtil.error("Failed to open scripts folder: %s", e.getMessage());
        }
    });

    public ScriptOptionsMod() {
        this.javaOptions.addProperties(this.javaUnsafe, this.javaGameAccess);
        this.luaOptions.addProperties(this.luaHttp);
        this.addProperties(this.javaOptions, this.luaOptions, this.reloadScripts, this.openScriptsFolder);
    }

    @Override
    public void onEnable() {
        this.toggle(false);
    }



}
