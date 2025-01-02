package wtf.bhopper.nonsense.event.impl.packet;

import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.bhopper.nonsense.event.Cancellable;

public class EventTeleport extends Cancellable {

    public C03PacketPlayer response;
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;

    public EventTeleport(C03PacketPlayer response, double x, double y, double z, float yaw, float pitch) {
        this.response = response;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

}
