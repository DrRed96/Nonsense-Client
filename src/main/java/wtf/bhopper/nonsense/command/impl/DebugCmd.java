package wtf.bhopper.nonsense.command.impl;

import net.minecraft.command.CommandBase;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandManager;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.module.impl.other.Debugger;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;

public class DebugCmd extends Command {

    public DebugCmd() {
        super("Debug", "Helps with debugging", ".debug <args>");
    }



    @Override
    public void onCommand(String[] args, String rawCommand) throws Exception {

        if (args.length < 2) {
            ChatUtil.error("Invalid arguments");
            return;
        }

        String subCommand = args[1];

        if (subCommand.equalsIgnoreCase("notification")) {
            if (args.length < 6) {
                ChatUtil.error("Invalid arguments");
                return;
            }

            String title = args[2];
            String message = args[3];
            NotificationType type = NotificationType.valueOf(args[4].toUpperCase());
            int displayTimeMS = CommandBase.parseInt(args[5], 1, 30000);
            Notification.send(title, message, type, displayTimeMS);

        } else if (subCommand.equalsIgnoreCase("packet")) {
            if (args.length < 3) {
                ChatUtil.error("Invalid arguments");
                return;
            }
            try {
                int hash = Integer.parseInt(args[2]);
                Debugger.PacketInfo packet = Nonsense.module(Debugger.class).cachedPacket(hash);
                if (packet == null) {
                    ChatUtil.error("That packet was not found");
                    return;
                }
                packet.print();

            } catch (NumberFormatException exception) {
                ChatUtil.error("'%s' is not a number", args[2]);
            }
        } else {
            ChatUtil.error("Invalid arguments");
        }

    }
}
