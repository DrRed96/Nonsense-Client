package wtf.bhopper.nonsense.anticheat.checks;

import net.minecraft.entity.player.EntityPlayer;
import wtf.bhopper.nonsense.anticheat.Check;
import wtf.bhopper.nonsense.anticheat.CheckInfo;
import wtf.bhopper.nonsense.anticheat.CheckResult;
import wtf.bhopper.nonsense.anticheat.PlayerData;

@CheckInfo(name = "Sprint", classifier = "A", description = "Incorrect direction while sprinting.", maxViolations = 10, unreliable = true)
public class SprintA extends Check {
    @Override
    protected CheckResult performCheck(EntityPlayer player, PlayerData data) {
        if (player.isSprinting() && player.onGround && data.forward < 0.0F) {
            return CheckResult.VIOLATE;
        }
        return CheckResult.RESET;
    }
}
