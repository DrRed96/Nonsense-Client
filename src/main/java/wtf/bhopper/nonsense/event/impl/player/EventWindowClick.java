package wtf.bhopper.nonsense.event.impl.player;

import wtf.bhopper.nonsense.event.Cancellable;

import java.util.ArrayList;
import java.util.List;

public class EventWindowClick extends Cancellable {

    public static boolean wasTriggeredInWindow = false;

    public final int trueWindowId;
    public int windowId;
    public int slotId;
    public int button;
    public int mode;
    public final List<InventoryAction> secondaryActions = new ArrayList<>(); // Any further actions to be made, usage of this may flag certain anti-cheats

    public EventWindowClick(int windowId) {
        this.trueWindowId = windowId;
        this.windowId = -1;
        this.slotId = -1;
        this.button = -1;
        this.mode = -1;
    }

    public EventWindowClick(int windowId, int slotId, int button, int mode) {
        this.trueWindowId = this.windowId = windowId;
        this.slotId = slotId;
        this.button = button;
        this.mode = mode;
    }

    public void addSecondaryAction(int windowId, int slot, int mouseButtonClicked, int mode) {
        this.secondaryActions.add(new InventoryAction(windowId, slot, mouseButtonClicked, mode));
    }

    public record InventoryAction(int windowId, int slot, int button, int mode) { }

}
