package wtf.bhopper.nonsense.command.impl;

import net.minecraft.command.CommandBase;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;

@CommandInfo(name = "VClip", description = "Vertically clip", syntax = ".vclip <blocks>")
public class Vclip extends Command {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {
        if (args.length < 2) {
            ChatUtil.error("Invalid arguments: %s", syntax);
            return;
        }

        double offset = CommandBase.parseDouble(args[1]);
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ);
        ChatUtil.info("Teleported %s %s blocks", offset >= 0.0 ? "up" : "down", offset, String.valueOf(offset));
    }
}
