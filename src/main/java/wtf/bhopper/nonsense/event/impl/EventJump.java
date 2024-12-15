package wtf.bhopper.nonsense.event.impl;

import wtf.bhopper.nonsense.event.Cancellable;

public class EventJump extends Cancellable {
    public float motion;
    public float yaw;

    public EventJump(float motion) {
        this.motion = motion;
    }

}
