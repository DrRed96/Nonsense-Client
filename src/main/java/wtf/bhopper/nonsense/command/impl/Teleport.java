package wtf.bhopper.nonsense.command.impl;

import net.minecraft.command.CommandBase;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

@CommandInfo(name = "Teleport", description = "Teleports you", syntax = ".tp <x> <y> <z>", alias = "tp")
public class Teleport extends Command {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {

        if (args.length < 3) {
            ChatUtil.error("Missing arguments: %s", this.syntax);
            return;
        }

        Vec3 position;

        if (args.length == 3) {
            position = new Vec3(
                    CommandBase.parseDouble(args[1], -6.0E7, 6.0E7),
                    128.0,
                    CommandBase.parseDouble(args[2], -6.0E7, 6.0E7)
            );
        } else {
            position = new Vec3(
                    CommandBase.parseDouble(args[1], -6.0E7, 6.0E7),
                    CommandBase.parseDouble(args[2], 0, 256),
                    CommandBase.parseDouble(args[3], -6.0E7, 6.0E7)
            );
        }

        mc.thePlayer.setPositionAndUpdate(position.xCoord, position.yCoord, position.zCoord);
        mc.renderGlobal.loadRenderers();

    }
}
