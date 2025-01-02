package wtf.bhopper.nonsense.event.impl.packet;

import wtf.bhopper.nonsense.event.Event;

public class EventActionStates implements Event {

    public boolean sprinting;
    public boolean sneaking;

    public EventActionStates(boolean sprinting, boolean sneaking) {
        this.sprinting = sprinting;
        this.sneaking = sneaking;
    }

}
