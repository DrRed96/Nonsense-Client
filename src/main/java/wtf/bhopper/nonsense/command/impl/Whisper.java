package wtf.bhopper.nonsense.command.impl;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.AbstractCommand;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.universe.packet.impl.client.C2SPacketWhisper;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

@CommandInfo(name = "Whisper",
        description = "Whisper something to another user",
        syntax = ".w <message>",
        alias = {"w", "message", "msg"})
public class Whisper extends AbstractCommand {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {

        if (!Nonsense.getUniverse().isConnected()) {
            ChatUtil.error("You must be connected to the Nonsense Universe (IRC) to run this command.");
            return;
        }

        if (args.length < 3) {
            ChatUtil.error("Missing arguments: %s", this.syntax);
            return;
        }

        String user = args[1];

        String[] messageArray = new String[args.length - 2];
        System.arraycopy(args, 2, messageArray, 0, messageArray.length);
        String message = String.join(" ", messageArray);

        Nonsense.getUniverse().sendPacket(new C2SPacketWhisper(user, message));
    }
}
