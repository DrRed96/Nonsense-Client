package wtf.bhopper.nonsense.event.impl;

import wtf.bhopper.nonsense.event.Cancellable;

public class EventSpeed extends Cancellable {

    public double speed;
    public float yaw;
    public double forward;
    public double strafe;

    public EventSpeed(double speed, float yaw, double forward, double strafe) {
        this.speed = speed;
        this.yaw = yaw;
        this.forward = forward;
        this.strafe = strafe;
    }

}
