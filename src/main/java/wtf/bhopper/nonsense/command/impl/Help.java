package wtf.bhopper.nonsense.command.impl;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandExecutionException;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;

@CommandInfo(name = "Help", description = "Displays this message.", syntax = ".help", alias = "?")
public class Help extends Command {

    @Override
    public void execute(String[] args, String rawCommand) throws CommandExecutionException {
        for (Command command : Nonsense.getCommandManager().getCommands()) {
            ChatUtil.print("\247c%s\2477: %s", command.name, command.description);
        }
    }

}
