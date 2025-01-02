package wtf.bhopper.nonsense.command.impl;

import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;

@CommandInfo(name = "Clear",
        description = "Clears the chat",
        syntax = ".clear")
public class Clear extends Command {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {
        mc.ingameGUI.getChatGUI().clearChatMessages();
    }
}
