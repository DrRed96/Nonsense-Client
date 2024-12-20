package wtf.bhopper.nonsense.event.impl.player;

import net.minecraft.util.MovingObjectPosition;
import wtf.bhopper.nonsense.event.Cancellable;

public class EventPreClick extends Cancellable {

    public final Button button;
    public final boolean artificial;
    public MovingObjectPosition mouseOver;

    public EventPreClick(Button button, boolean artificial, MovingObjectPosition mouseOver) {
        this.button = button;
        this.artificial = artificial;
        this.mouseOver = mouseOver;
    }

    public enum Button {
        LEFT,
        RIGHT
    }

}
