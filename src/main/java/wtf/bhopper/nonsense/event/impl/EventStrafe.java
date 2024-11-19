package wtf.bhopper.nonsense.event.impl;

import wtf.bhopper.nonsense.event.Event;

public class EventStrafe implements Event {

    public float forward;
    public float strafe;
    public float friction;
    public float yaw;

    public EventStrafe(float forward, float strafe, float friction, float yaw) {
        this.forward = forward;
        this.strafe = strafe;
        this.friction = friction;
        this.yaw = yaw;
    }

}
