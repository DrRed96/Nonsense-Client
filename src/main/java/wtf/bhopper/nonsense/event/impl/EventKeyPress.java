package wtf.bhopper.nonsense.event.impl;

import wtf.bhopper.nonsense.event.Event;

public class EventKeyPress implements Event {

    public final int key;

    public EventKeyPress(int key) {
        this.key = key;
    }

}
