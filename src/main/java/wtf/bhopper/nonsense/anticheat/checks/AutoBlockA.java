package wtf.bhopper.nonsense.anticheat.checks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S0BPacketAnimation;
import wtf.bhopper.nonsense.anticheat.Check;
import wtf.bhopper.nonsense.anticheat.CheckInfo;
import wtf.bhopper.nonsense.anticheat.CheckResult;
import wtf.bhopper.nonsense.anticheat.PlayerData;

@CheckInfo(name = "Auto Block",
        classifier = "A",
        description = "Swing shouldn't have been allowed.")
public class AutoBlockA extends Check {
    @Override
    protected CheckResult check(EntityPlayer player, PlayerData data, Packet<?> packet) {
        if (packet instanceof S0BPacketAnimation p) {
            if (p.getEntityID() == player.getEntityId()) {
                if (p.getAnimationType() == 0 && player.isUsingItem()) {
                    return CheckResult.VIOLATE;
                }
            }
        }

        return CheckResult.IDLE;
    }
}
