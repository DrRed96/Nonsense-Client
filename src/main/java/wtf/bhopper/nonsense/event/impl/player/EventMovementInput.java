package wtf.bhopper.nonsense.event.impl.player;

import wtf.bhopper.nonsense.event.Event;

public class EventMovementInput implements Event {

    public float forwards;
    public float strafe;
    public boolean jump;
    public boolean sneak;

    public EventMovementInput(float forwards, float strafe, boolean jump, boolean sneak) {
        this.forwards = forwards;
        this.strafe = strafe;
        this.jump = jump;
        this.sneak = sneak;
    }

}
