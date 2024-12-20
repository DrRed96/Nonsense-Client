package wtf.bhopper.nonsense.event.impl.player;

import wtf.bhopper.nonsense.event.Event;
import wtf.bhopper.nonsense.util.minecraft.player.Rotation;

public class EventPreMotion implements Event {

    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public boolean onGround;

    public EventPreMotion(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public void setRotations(Rotation rotation) {
        this.yaw = rotation.yaw;
        this.pitch = rotation.pitch;
    }

}
