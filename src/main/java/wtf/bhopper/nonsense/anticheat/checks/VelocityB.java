package wtf.bhopper.nonsense.anticheat.checks;

import net.minecraft.entity.player.EntityPlayer;
import wtf.bhopper.nonsense.anticheat.Check;
import wtf.bhopper.nonsense.anticheat.CheckInfo;
import wtf.bhopper.nonsense.anticheat.CheckResult;
import wtf.bhopper.nonsense.anticheat.PlayerData;

@CheckInfo(name = "Velocity", classifier = "B", description = "Vertical velocity")
public class VelocityB extends Check {
    @Override
    protected CheckResult performCheck(EntityPlayer player, PlayerData data) {
        if (player.hurtResistantTime > 6 && player.hurtResistantTime < 12) {
            if (data.moveY == 0.0) {
                return CheckResult.VIOLATE;
            }
        }
        return CheckResult.IDLE;
    }
}
