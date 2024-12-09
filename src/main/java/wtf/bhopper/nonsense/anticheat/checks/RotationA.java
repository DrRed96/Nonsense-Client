package wtf.bhopper.nonsense.anticheat.checks;

import net.minecraft.entity.player.EntityPlayer;
import wtf.bhopper.nonsense.anticheat.Check;
import wtf.bhopper.nonsense.anticheat.CheckInfo;
import wtf.bhopper.nonsense.anticheat.CheckResult;
import wtf.bhopper.nonsense.anticheat.PlayerData;

@CheckInfo(name = "Rotation", classifier = "A", description = "Impossible rotations (-90 \u2264 pitch \u2264 90).", maxViolations = 1)
public class RotationA extends Check {
    @Override
    protected CheckResult performCheck(EntityPlayer player, PlayerData data) {
        if (player.rotationPitch > 90.0F || player.rotationPitch < -90.0F) {
            return CheckResult.VIOLATE;
        }
        return CheckResult.RESET;
    }
}
