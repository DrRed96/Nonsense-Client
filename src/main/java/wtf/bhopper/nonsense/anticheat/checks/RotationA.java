package wtf.bhopper.nonsense.anticheat.checks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S14PacketEntity;
import wtf.bhopper.nonsense.anticheat.Check;
import wtf.bhopper.nonsense.anticheat.CheckInfo;
import wtf.bhopper.nonsense.anticheat.CheckResult;
import wtf.bhopper.nonsense.anticheat.PlayerData;

@CheckInfo(name = "Rotation",
        classifier = "A",
        description = "Impossible pitch rotation.",
        maxViolations = 1)
public class RotationA extends Check {
    @Override
    protected CheckResult check(EntityPlayer player, PlayerData data, Packet<?> packet) {
        if (packet instanceof S14PacketEntity p) {
            if (p.getEntity(mc.theWorld) == player && p.didRotate()) {
                float pitch = (float)(p.getPitch() * 360) / 256.0F;
                if (pitch < -90.0F || pitch > 90.0F) {
                    return CheckResult.VIOLATE;
                }
            }
        }

        return CheckResult.RESET;
    }
}
