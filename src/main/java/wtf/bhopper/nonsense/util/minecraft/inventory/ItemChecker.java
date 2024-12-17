package wtf.bhopper.nonsense.util.minecraft.inventory;

import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface ItemChecker {
    boolean isValid(ItemStack stack);
}
