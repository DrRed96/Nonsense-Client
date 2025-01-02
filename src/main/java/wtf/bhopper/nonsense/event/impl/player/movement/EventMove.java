package wtf.bhopper.nonsense.event.impl.player.movement;

import wtf.bhopper.nonsense.event.Event;

public class EventMove implements Event {

    public double x;
    public double y;
    public double z;

    public EventMove(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

}
