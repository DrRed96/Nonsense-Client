package wtf.bhopper.nonsense.command.impl;

import net.minecraft.client.gui.GuiScreen;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;

@CommandInfo(name = "Copy",
        description = "Copies text to the clipboard (used in copy chat click events)",
        syntax = ".copy <text>")
public class Copy extends Command {


    @Override
    public void execute(String[] args, String rawCommand) throws Exception {
        if (args.length < 2) {
            return;
        }

        String text = rawCommand.substring(args[0].length() + 1);
        GuiScreen.setClipboardString(text);
        ChatUtil.print("\247cCopied: \2477%s", text);
    }
}
