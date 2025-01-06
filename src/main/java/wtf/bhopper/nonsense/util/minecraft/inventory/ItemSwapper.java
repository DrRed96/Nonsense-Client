package wtf.bhopper.nonsense.util.minecraft.inventory;

import java.util.function.Supplier;

public record ItemSwapper(ItemTracker tracker, Supplier<Integer> targetSlot) {
}
