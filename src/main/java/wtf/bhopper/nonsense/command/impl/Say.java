package wtf.bhopper.nonsense.command.impl;

import wtf.bhopper.nonsense.command.AbstractCommand;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

@CommandInfo(name = "Say",
        description = "Send a message in chat",
        syntax = ".say <message>",
        alias = "chat")
public class Say extends AbstractCommand {

    @Override
    public void execute(String[] args, String rawCommand) throws Exception {
        if (args.length < 2) {
            ChatUtil.error("Missing arguments: %s", this.syntax);
            return;
        }

        ChatUtil.sendNoEvent("%s", rawCommand.substring(args[0].length() + 1));

    }
}
