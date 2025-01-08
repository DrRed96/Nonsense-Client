package wtf.bhopper.nonsense.event.impl.player.inventory;

import wtf.bhopper.nonsense.event.Event;

public class EventSelectItem implements Event {

    public int slot;
    public final int prevSlot;
    public boolean silent;
    public final boolean swapped;

    public EventSelectItem(int slot, int prevSlot, boolean swapped) {
        this.slot = slot;
        this.prevSlot = prevSlot;
        this.silent = false;
        this.swapped = swapped;
    }

}
