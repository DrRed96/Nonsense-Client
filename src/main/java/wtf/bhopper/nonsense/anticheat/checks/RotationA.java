package wtf.bhopper.nonsense.anticheat.checks;

import net.minecraft.entity.player.EntityPlayer;
import wtf.bhopper.nonsense.anticheat.Check;
import wtf.bhopper.nonsense.anticheat.CheckInfo;
import wtf.bhopper.nonsense.anticheat.CheckResult;
import wtf.bhopper.nonsense.anticheat.PlayerData;

@CheckInfo(name = "Rotation", classifier = "A", description = "Impossible pitch rotation.", maxViolations = 1)
public class RotationA extends Check {
    @Override
    protected CheckResult performCheck(EntityPlayer player, PlayerData data) {
        if (player.rotationPitch > 90.0F || player.rotationPitch < -90.0F) {
            return CheckResult.VIOLATE;
        }
        return CheckResult.RESET;
    }
}
