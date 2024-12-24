package wtf.bhopper.nonsense.anticheat.checks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import wtf.bhopper.nonsense.anticheat.Check;
import wtf.bhopper.nonsense.anticheat.CheckInfo;
import wtf.bhopper.nonsense.anticheat.CheckResult;
import wtf.bhopper.nonsense.anticheat.PlayerData;

@CheckInfo(name = "Invalid",
        classifier = "A",
        description = "Impossible \"Sneaking\" and \"Sprinting\" status combination.",
        maxViolations = 10)
public class InvalidA extends Check {
    @Override
    protected CheckResult check(EntityPlayer player, PlayerData data, Packet<?> packet) {
        if (isUpdate(player, packet)) {
            if (player.isSneaking() && player.isSprinting()) {
                return CheckResult.VIOLATION;
            }
        }

        return CheckResult.IDLE;
    }
}
