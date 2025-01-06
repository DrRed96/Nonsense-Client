package wtf.bhopper.nonsense.util.minecraft.inventory;

import net.minecraft.item.ItemStack;

public class ItemSlot {

    public final ItemStack stack;
    public int slot;

    public ItemSlot(ItemStack stack, int slot) {
        this.stack = stack;
        this.slot = slot;
    }

}
