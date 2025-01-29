package wtf.bhopper.nonsense.component.impl.player;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.component.AbstractComponent;

public class SilentSlotComponent extends AbstractComponent {

    public int slot = 0;

    public static int getSlot() {
        return Nonsense.component(SilentSlotComponent.class).slot;
    }

    public static void setSlot(int slot) {
        Nonsense.component(SilentSlotComponent.class).slot = slot;
    }

}
