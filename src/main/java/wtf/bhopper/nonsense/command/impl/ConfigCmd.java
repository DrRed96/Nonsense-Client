package wtf.bhopper.nonsense.command.impl;

import net.minecraft.event.ClickEvent;
import net.minecraft.util.EnumChatFormatting;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandExecutionException;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.config.Config;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;

import java.io.IOException;

@CommandInfo(name = "Config",
        description = "Manage configs",
        syntax = ".config <load/save/del> <name> | .config list",
        alias = {"properties", "settings", "c", "s", "p"})
public class ConfigCmd extends Command {
    @Override
    public void execute(String[] args, String rawCommand) throws CommandExecutionException {

        if (args.length < 2) {
            ChatUtil.error("Missing arguments: %s", this.syntax);
            return;
        }

        switch (args[1].toLowerCase()) {
            case "load" -> {
                if (args.length < 3) {
                    ChatUtil.error("Missing arguments: %s", this.syntax);
                    return;
                }

                if (Nonsense.getConfigManager().hasConfig(args[2])) {
                    try {
                        Nonsense.getConfigManager().getConfig(args[2]).load();
                        ChatUtil.info("Loaded config: %s", args[2]);
                    } catch (IOException exception) {
                        ChatUtil.error("Failed to load config: %s", exception.getMessage());
                    }
                } else {
                    ChatUtil.error("Config: %s, does not exist.", args[2]);
                }
            }

            case "save" -> {
                if (args.length < 3) {
                    ChatUtil.error("Missing arguments: %s", this.syntax);
                    return;
                }

                try {
                    Nonsense.getConfigManager().getConfig(args[2]).save();
                    ChatUtil.info("Saved config: %s", args[2]);
                } catch (IOException exception) {
                    ChatUtil.error("Failed to save config: %s", exception.getMessage());
                }

            }

            case "list" -> {
                Nonsense.getConfigManager().reloadConfigs();

                if (Nonsense.getConfigManager().configs.isEmpty()) {
                    ChatUtil.error("You don't have any configs :(");
                    return;
                }

                int count = 1;
                for (Config config : Nonsense.getConfigManager().configs) {
                    ChatUtil.Builder.of("%s\247c%d: \2477%s", ChatUtil.CHAT_PREFIX_SHORT, count, config.name)
                            .setColor(EnumChatFormatting.GRAY)
                            .setHoverEvent("Click to load: " + config.name)
                            .setClickEvent(ClickEvent.Action.RUN_COMMAND, ".config load " + config.name)
                            .send();
                    count++;
                }

            }
        }

    }
}
