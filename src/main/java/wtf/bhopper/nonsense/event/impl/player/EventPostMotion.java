package wtf.bhopper.nonsense.event.impl.player;

import wtf.bhopper.nonsense.event.Event;

public class EventPostMotion implements Event {

    public final double x;
    public final double y;
    public final double z;
    public final float yaw;
    public final float pitch;
    public final boolean onGround;

    public EventPostMotion(EventPreMotion eventPre) {
        this.x = eventPre.x;
        this.y = eventPre.y;
        this.z = eventPre.z;
        this.yaw = eventPre.yaw;
        this.pitch = eventPre.pitch;
        this.onGround = eventPre.onGround;
    }

}
