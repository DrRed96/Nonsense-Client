package wtf.bhopper.nonsense.event.impl;

import wtf.bhopper.nonsense.event.Event;

public class EventPostStep implements Event {

    public double height;
    public double realHeight;

    public EventPostStep(double height, double realHeight) {
        this.height = height;
        this.realHeight = realHeight;
    }


}
