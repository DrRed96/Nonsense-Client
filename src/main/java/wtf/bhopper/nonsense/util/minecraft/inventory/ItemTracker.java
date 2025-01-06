package wtf.bhopper.nonsense.util.minecraft.inventory;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ItemTracker {

    private final ItemChecker checker;
    private final ItemScoreCalculator scoreCalculator;
    private final Supplier<Integer> amountChecker;
    private final Supplier<Boolean> shouldDrop;

    private final List<ItemSlot> checkedItems = new ArrayList<>();

    public ItemTracker(ItemChecker checker, ItemScoreCalculator scoreCalculator, Supplier<Integer> amountChecker, Supplier<Boolean> shouldDrop) {
        this.checker = checker;
        this.scoreCalculator = scoreCalculator;
        this.amountChecker = amountChecker;
        this.shouldDrop = shouldDrop;
    }

    public boolean check(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        return this.checker.check(itemStack);
    }

    public float getScore(ItemStack itemStack) {
        if (itemStack == null) {
            return -1.0F;
        }

        return this.scoreCalculator.getScore(itemStack);
    }

    public int getAmountToKeep() {
        return this.amountChecker.get();
    }

    public boolean dropEnabled() {
        return this.shouldDrop.get();
    }

    public void addItem(ItemSlot itemSlot) {
        for (int i = 0; i < this.checkedItems.size(); i++) {
            ItemSlot checkedItem = this.checkedItems.get(i);
            if (this.getScore(itemSlot.stack) <= this.getScore(checkedItem.stack)) {
                checkedItems.add(i, itemSlot);
                return;
            }
        }

        this.checkedItems.add(itemSlot);
    }

    public boolean addItemIfChecked(ItemSlot itemSlot) {
        if (this.check(itemSlot.stack)) {
            this.addItem(itemSlot);
            return true;
        }

        return false;
    }

    public int getAmountOfItems() {
        return this.checkedItems.size();
    }

    public ItemSlot get(int i) {
        return this.checkedItems.get(i);
    }

    public ItemSlot getFirst() {
        return this.checkedItems.getFirst();
    }

    public ItemSlot getLast() {
        return this.checkedItems.getLast();
    }

    public ItemSlot removeFirst() {
        return this.checkedItems.removeFirst();
    }

    public ItemSlot removeLast() {
        return this.checkedItems.removeLast();
    }

    public int size() {
        return this.checkedItems.size();
    }

    public List<ItemSlot> getItems() {
        return this.checkedItems;
    }

    public void clear() {
        this.checkedItems.clear();
    }

}
