package wtf.bhopper.nonsense.command.impl;

import net.minecraft.event.ClickEvent;
import net.minecraft.util.EnumChatFormatting;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.config.Config;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;

import java.io.IOException;

@CommandInfo(name = "Config",
        description = "Manage configs",
        syntax = ".config <load/save/del> <name> | .config list",
        alias = {"properties", "settings", "c", "s", "p"})
public class ConfigCmd extends Command {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {

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
                        Notification.send("Config Manager", "Loaded config: " + args[2], NotificationType.SUCCESS, 3000);
                    } catch (IOException exception) {
                        Notification.send("Config Manager", "Failed to load config: " + exception.getMessage(), NotificationType.ERROR, 3000);
                    }
                } else {
                    Notification.send("Config Manager", "Config: " + args[2] + ", does not exist.", NotificationType.ERROR, 3000);
                }
                Nonsense.getConfigManager().reloadConfigs();
            }

            case "save" -> {
                if (args.length < 3) {
                    ChatUtil.error("Missing arguments: %s", this.syntax);
                    return;
                }

                try {
                    Nonsense.getConfigManager().getConfig(args[2]).save();
                    Notification.send("Config Manager", "Saved config: " + args[2], NotificationType.SUCCESS, 3000);
                } catch (IOException exception) {
                    Notification.send("Config Manager", "Failed to save config: " + exception.getMessage(), NotificationType.ERROR, 3000);
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
