package wtf.bhopper.nonsense.anticheat.check.impl;

import net.minecraft.network.play.server.S14PacketEntity;
import wtf.bhopper.nonsense.anticheat.PlayerData;
import wtf.bhopper.nonsense.anticheat.check.AbstractCheck;
import wtf.bhopper.nonsense.anticheat.check.CheckInfo;

@CheckInfo(name = "Rotation",
        description = "Impossible pitch rotation.",
        maxVl = 1)
public class RotationA extends AbstractCheck {

    @Override
    public void handleRelMove(PlayerData data, S14PacketEntity packet) {
        if (packet.isRotating()) {
            if (Math.abs(data.getRotationPitch()) > 90.0F) {
                data.incrementVl(this);
            }
        }
    }
}
