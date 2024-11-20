package wtf.bhopper.nonsense.command.impl;

import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;

@CommandInfo(name = "Say", description = "Send a message in chat", syntax = ".say <message>", alias = "chat")
public class Say extends Command {

    @Override
    public void execute(String[] args, String rawCommand) throws Exception {
        if (args.length < 2) {
            ChatUtil.error("Missing arguments: %s", this.syntax);
            return;
        }

        ChatUtil.sendNoEvent("%s", rawCommand.substring(args[0].length() + 1));

    }
}
