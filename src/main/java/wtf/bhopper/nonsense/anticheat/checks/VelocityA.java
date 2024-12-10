package wtf.bhopper.nonsense.anticheat.checks;

import net.minecraft.entity.player.EntityPlayer;
import wtf.bhopper.nonsense.anticheat.Check;
import wtf.bhopper.nonsense.anticheat.CheckInfo;
import wtf.bhopper.nonsense.anticheat.CheckResult;
import wtf.bhopper.nonsense.anticheat.PlayerData;

@CheckInfo(name = "Velocity", classifier = "A", description = "No horizontal motion in response to damage")
public class VelocityA extends Check {
    @Override
    protected CheckResult performCheck(EntityPlayer player, PlayerData data) {
        if (player.hurtResistantTime > 6 && player.hurtResistantTime < 12) {
            if (data.moveX == 0.0 && data.moveZ == 0.0 && !mc.theWorld.checkBlockCollision(player.getEntityBoundingBox().expand(0.05, 0.0, 0.05))) {
                return CheckResult.VIOLATE;
            }
        }
        return CheckResult.IDLE;
    }
}
