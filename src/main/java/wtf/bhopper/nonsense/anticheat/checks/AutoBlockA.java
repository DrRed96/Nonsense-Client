package wtf.bhopper.nonsense.anticheat.checks;

import net.minecraft.entity.player.EntityPlayer;
import wtf.bhopper.nonsense.anticheat.Check;
import wtf.bhopper.nonsense.anticheat.CheckInfo;
import wtf.bhopper.nonsense.anticheat.CheckResult;
import wtf.bhopper.nonsense.anticheat.PlayerData;

@CheckInfo(name = "Auto Block", classifier = "A", description = "Swing shouldn't have been allowed.", maxViolations = 10)
public class AutoBlockA extends Check {
    @Override
    protected CheckResult performCheck(EntityPlayer player, PlayerData data) {
        if (player.isSwingInProgress && player.swingProgressInt == -1 && player.isBlocking()) {
            return CheckResult.VIOLATE;
        }
        return CheckResult.IDLE;
    }
}
