package wtf.bhopper.nonsense.event.impl.player;

import net.minecraft.util.MovingObjectPosition;
import wtf.bhopper.nonsense.event.Event;

public class EventPostClick implements Event {

    public final Button button;
    public final boolean artificial;
    public final MovingObjectPosition mouseOver;

    public EventPostClick(Button button, boolean artificial, MovingObjectPosition mouseOver) {
        this.button = button;
        this.artificial = artificial;
        this.mouseOver = mouseOver;
    }

    public enum Button {
        LEFT,
        RIGHT
    }

}
