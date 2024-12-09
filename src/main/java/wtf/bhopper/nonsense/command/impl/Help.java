package wtf.bhopper.nonsense.command.impl;

import net.minecraft.event.ClickEvent;
import net.minecraft.util.EnumChatFormatting;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;

@CommandInfo(name = "Help", description = "Displays this message.", syntax = ".help", alias = "?")
public class Help extends Command {

    @Override
    public void execute(String[] args, String rawCommand) throws Exception {
        ChatUtil.title("Commands");
        for (Command command : Nonsense.getCommandManager().getCommands()) {

            ChatUtil.Builder.of("%s\247c%s\2477: %s", ChatUtil.CHAT_PREFIX_SHORT, command.name, command.description)
                    .setColor(EnumChatFormatting.GRAY)
                    .setHoverEvent(
                            GeneralUtil.paragraph(
                                    "\247c\247l" + command.name + " \247r\2477(" + String.join(", ", command.alias) + ")",
                                    "\2477" + command.description,
                                    "\2477" + command.syntax
                            )
                    )
                    .setClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "." + command.name.toLowerCase())
                    .send();
        }
    }

}
