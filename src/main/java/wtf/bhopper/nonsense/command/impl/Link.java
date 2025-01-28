package wtf.bhopper.nonsense.command.impl;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.universe.packet.impl.client.C2SPacketRequestDiscordLink;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

@CommandInfo(name = "Link",
        description = "Get a code to link your discord account to your Nonsense IRC account",
        syntax = ".link")
public class Link extends Command {

    @Override
    public void execute(String[] args, String rawCommand) throws Exception {
        if (!Nonsense.getUniverse().isConnected()) {
            ChatUtil.error("You must be connected to the Nonsense Universe (IRC) to run this command.");
            return;
        }

        Nonsense.getUniverse().sendPacket(new C2SPacketRequestDiscordLink());
    }
}
