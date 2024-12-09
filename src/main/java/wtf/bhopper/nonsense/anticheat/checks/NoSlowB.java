package wtf.bhopper.nonsense.anticheat.checks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import wtf.bhopper.nonsense.anticheat.Check;
import wtf.bhopper.nonsense.anticheat.CheckInfo;
import wtf.bhopper.nonsense.anticheat.CheckResult;
import wtf.bhopper.nonsense.anticheat.PlayerData;

@CheckInfo(name = "No Slow", classifier = "B", description = "Moving too fast while using an item.", maxViolations = 30)
public class NoSlowB extends Check {
    @Override
    protected CheckResult performCheck(EntityPlayer player, PlayerData data) {
        if (player.getActivePotionEffect(Potion.moveSpeed) != null) {
            return CheckResult.IDLE; // Could false flag if the player has speed
        }

        if (player.isUsingItem() && data.moveX > 0.3 && data.moveZ > 0.3) {
            return CheckResult.VIOLATE;
        }

        return CheckResult.RESET;
    }
}
