package wtf.bhopper.nonsense.anticheat.checks;

import net.minecraft.entity.player.EntityPlayer;
import wtf.bhopper.nonsense.anticheat.Check;
import wtf.bhopper.nonsense.anticheat.CheckInfo;
import wtf.bhopper.nonsense.anticheat.CheckResult;
import wtf.bhopper.nonsense.anticheat.PlayerData;

@CheckInfo(name = "No Slow", classifier = "A", description = "Using an item while sprinting.", maxViolations = 10)
public class NoSlowA extends Check {
    @Override
    protected CheckResult performCheck(EntityPlayer player, PlayerData data) {
        if (player.isUsingItem() && player.isSprinting()) {
            return CheckResult.VIOLATE;
        }
        return CheckResult.RESET;
    }
}
