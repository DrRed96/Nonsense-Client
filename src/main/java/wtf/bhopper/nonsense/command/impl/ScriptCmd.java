package wtf.bhopper.nonsense.command.impl;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

@CommandInfo(name = "Script",
        description = "Configure scripts",
        syntax = ".script <args>")
public class ScriptCmd extends Command {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {
        switch (args[1].toLowerCase()) {
            case "reload" -> Nonsense.getScriptManager().loadScripts(true);
            default -> ChatUtil.error("Invalid arguments.");
        }
    }
}
