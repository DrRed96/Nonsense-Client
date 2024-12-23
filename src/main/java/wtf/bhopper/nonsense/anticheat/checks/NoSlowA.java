package wtf.bhopper.nonsense.anticheat.checks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import wtf.bhopper.nonsense.anticheat.Check;
import wtf.bhopper.nonsense.anticheat.CheckInfo;
import wtf.bhopper.nonsense.anticheat.CheckResult;
import wtf.bhopper.nonsense.anticheat.PlayerData;

@CheckInfo(name = "No Slow",
        classifier = "A",
        description = "Impossible combination of statuses.",
        maxViolations = 10)
public class NoSlowA extends Check {
    @Override
    protected CheckResult check(EntityPlayer player, PlayerData data, Packet<?> packet) {
        if (isUpdate(player, packet)) {
            if (player.isSprinting() && player.isEating()) {
                return CheckResult.VIOLATE;
            }
            return CheckResult.RESET;
        }

        return CheckResult.IDLE;
    }
}
