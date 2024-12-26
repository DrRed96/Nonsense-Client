package wtf.bhopper.nonsense.anticheat.check.impl;

import net.minecraft.network.play.server.S14PacketEntity;
import wtf.bhopper.nonsense.anticheat.PlayerData;
import wtf.bhopper.nonsense.anticheat.check.Check;
import wtf.bhopper.nonsense.anticheat.check.CheckInfo;

@CheckInfo(name = "Rotation",
        description = "Impossible pitch rotation.",
        maxVl = 1)
public class RotationA extends Check {

    @Override
    public void handleRelMove(PlayerData data, S14PacketEntity packet) {
        if (packet.isRotating()) {
            if (Math.abs(data.getRotationYaw()) > 90.0F) {
                data.incrementVl(this);
            }
        }
    }
}
