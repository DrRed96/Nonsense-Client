package wtf.bhopper.nonsense.event.impl.player.movement;

import wtf.bhopper.nonsense.event.Cancellable;

public class EventJump extends Cancellable {
    public float motion;
    public float yaw;

    public EventJump(float motion, float yaw) {
        this.motion = motion;
        this.yaw = yaw;
    }

}
