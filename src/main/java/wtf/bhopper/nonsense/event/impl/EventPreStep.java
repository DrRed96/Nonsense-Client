package wtf.bhopper.nonsense.event.impl;

import wtf.bhopper.nonsense.event.Event;

public class EventPreStep implements Event {

    public double height;

    public EventPreStep(double height) {
        this.height = height;
    }

}
