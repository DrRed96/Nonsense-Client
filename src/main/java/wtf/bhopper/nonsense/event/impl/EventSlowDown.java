package wtf.bhopper.nonsense.event.impl;

import wtf.bhopper.nonsense.event.Cancellable;

public class EventSlowDown extends Cancellable {

    public float factor;

    public EventSlowDown(float factor) {
        this.factor = factor;
    }

}
