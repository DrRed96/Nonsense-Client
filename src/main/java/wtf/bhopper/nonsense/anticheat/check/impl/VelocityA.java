package wtf.bhopper.nonsense.anticheat.check.impl;

import net.minecraft.network.play.server.S14PacketEntity;
import wtf.bhopper.nonsense.anticheat.PlayerData;
import wtf.bhopper.nonsense.anticheat.check.AbstractCheck;
import wtf.bhopper.nonsense.anticheat.check.CheckInfo;

@CheckInfo(name = "Velocity", description = "Horizontal velocity", maxVl = 10, enabled = false)
public class VelocityA extends AbstractCheck {

    @Override
    public void handleRelMove(PlayerData data, S14PacketEntity packet) {
        if (data.getDeltaXZ() == 0.0 &&
                data.getEntity().hurtResistantTime > 6 && data.getEntity().hurtResistantTime < 1 &&
                !mc.theWorld.checkBlockCollision(data.getEntity().getEntityBoundingBox().expand(0.05, 0.0, 0.05))) {
            data.incrementVl(this);
        }

        data.decrementVl(this, 0.5F);
    }
}
