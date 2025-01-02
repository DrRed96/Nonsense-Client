package wtf.bhopper.nonsense.event.impl.player.interact;

import net.minecraft.util.MovingObjectPosition;
import wtf.bhopper.nonsense.event.Event;

public class EventMouseOver implements Event {

    public MovingObjectPosition mouseOver;

    public EventMouseOver(MovingObjectPosition mouseOver) {
        this.mouseOver = mouseOver;
    }

}
