package wtf.bhopper.nonsense.event.impl.player.interact;

import net.minecraft.util.MovingObjectPosition;
import wtf.bhopper.nonsense.event.Cancellable;

public class EventClickAction extends Cancellable {

    public final boolean leftButton;
    public boolean left;
    public boolean leftSwing;

    public final boolean rightButton;
    public boolean right;
    public boolean rightSwing;

    public final boolean releaseButton;
    public boolean release;

    public MovingObjectPosition mouseOver;

    public final boolean usingItem;

    public EventClickAction(MovingObjectPosition mouseOver, boolean leftButton, boolean rightButton, boolean releaseButton, boolean usingItem) {
        this.mouseOver = mouseOver;
        this.leftButton = leftButton;
        this.left = !usingItem && leftButton;
        this.leftSwing = true;
        this.rightButton = rightButton;
        this.right = !usingItem && rightButton;
        this.rightSwing = true;
        this.releaseButton = releaseButton;
        this.release = usingItem && releaseButton;
        this.usingItem = usingItem;
    }

}
