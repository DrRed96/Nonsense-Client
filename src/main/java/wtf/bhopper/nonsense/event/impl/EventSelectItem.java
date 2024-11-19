package wtf.bhopper.nonsense.event.impl;

import wtf.bhopper.nonsense.event.Event;

public class EventSelectItem implements Event {

    public boolean silent;
    public int slot;

    public EventSelectItem(int slot) {
        this.slot = slot;
        this.silent = false;
    }

}
