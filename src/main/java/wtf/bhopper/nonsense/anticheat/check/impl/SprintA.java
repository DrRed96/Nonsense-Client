package wtf.bhopper.nonsense.anticheat.check.impl;

import net.minecraft.network.play.server.S14PacketEntity;
import wtf.bhopper.nonsense.anticheat.PlayerData;
import wtf.bhopper.nonsense.anticheat.check.AbstractCheck;
import wtf.bhopper.nonsense.anticheat.check.CheckInfo;
import wtf.bhopper.nonsense.anticheat.check.data.AbstractCheckBuffer;

@CheckInfo(name = "Sprint", description = "Bad direction while sprinting.", maxVl = 5)
public class SprintA extends AbstractCheck {


    @Override
    public void handleRelMove(PlayerData data, S14PacketEntity packet) {
        if (!data.isSprinting()) {
            if (data.hasCheckData(Buffer.class) && data.getCheckData(Buffer.class).getBuffer() > 0.0F) {
                data.getCheckData(Buffer.class).decrementBuffer(0.1F);
            }
            data.decrementVl(this, 0.985F);
            return;
        }

        if (Math.abs(data.getMoveYaw() - data.getRotationYaw()) > 90.0F && data.isGroundCollision() && data.getDeltaXZ() >= 0.2) {
            if (data.getCheckData(Buffer.class, Buffer::new).incrementBuffer() >= 20.0F) {
                data.incrementVl(this);
            }
        } else if (data.hasCheckData(Buffer.class) && data.getCheckData(Buffer.class).getBuffer() > 0.0F) {
            data.getCheckData(Buffer.class).decrementBuffer(0.1F);
        }

    }

    public static class Buffer extends AbstractCheckBuffer {}
}
