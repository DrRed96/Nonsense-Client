package wtf.bhopper.nonsense.command.impl;

import net.minecraft.client.gui.GuiScreen;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;

@CommandInfo(name = "Username",
        description = "Copies your username to the clipboard",
        syntax = ".name",
        alias = {"name", "ign"})
public class Username extends Command {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {
        GuiScreen.setClipboardString(mc.thePlayer.getName());
        Notification.send("Name", "Copies your username to the clipboard", NotificationType.SUCCESS, 3000);
    }
}
