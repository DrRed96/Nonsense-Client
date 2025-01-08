package wtf.bhopper.nonsense.event.impl.player.inventory;

import wtf.bhopper.nonsense.event.Cancellable;

public class EventChangeItem extends Cancellable {

    public int direction;

    public EventChangeItem(int direction) {
        this.direction = direction;
    }

}
