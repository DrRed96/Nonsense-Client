package wtf.bhopper.nonsense.module.impl.player;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.EventPriorities;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventUpdate;
import wtf.bhopper.nonsense.event.impl.player.inventory.EventWindowClick;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.gui.screens.creative.GuiCustomCreative;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.impl.movement.Scaffold;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.*;
import wtf.bhopper.nonsense.util.minecraft.inventory.*;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.Supplier;

/**
 * This module was made with help from 5kr411.
 */

@SuppressWarnings("FieldCanBeLocal")
@ModuleInfo(name = "Inventory Manager",
        description = "Manages your inventory.",
        category = ModuleCategory.PLAYER)
public class InventoryManager extends Module {

    public static final NumberFormat FORMAT_STACKS = new DecimalFormat("#0.## 'stacks'");

    private final List<ItemProperty> itemProperties = new ArrayList<>();

    private final GroupProperty slotsGroup = new GroupProperty("Slots", "Item slots.", this);
    private final NumberProperty swordSlot = new NumberProperty("Sword", "Sword slot.", 1, 0, 9, 1, NumberProperty.FORMAT_INT);
    private final NumberProperty bowSlot = new NumberProperty("Bow", "Bow slot.", 0, 0, 9, 1, NumberProperty.FORMAT_INT);
    private final NumberProperty fishingRodSlot = new NumberProperty("Fishing Rod", "Fishing rod slot.", 0, 0, 9, 1, NumberProperty.FORMAT_INT);
    private final NumberProperty enderPearlSlot = new NumberProperty("Ender Pearl", "Ender pearl slot.", 0, 0, 9, 1, NumberProperty.FORMAT_INT);
    private final NumberProperty goldenAppleSlot = new NumberProperty("Golden Apple", "Golden apple slot.", 9, 0, 9, 1, NumberProperty.FORMAT_INT);
    private final NumberProperty blocksSlot = new NumberProperty("Blocks", "Blocks slot.", 2, 0, 9, 1, NumberProperty.FORMAT_INT);
    private final NumberProperty pickaxeSlot = new NumberProperty("Pickaxe", "Pickaxe slot.", 0, 0, 9, 1, NumberProperty.FORMAT_INT);
    private final NumberProperty axeSlot = new NumberProperty("Axe", "Axe slot.", 0, 0, 9, 1, NumberProperty.FORMAT_INT);
    private final NumberProperty shovelSlot = new NumberProperty("Shovel", "Shovel slot.", 0, 0, 9, 1, NumberProperty.FORMAT_INT);

    private final GroupProperty autoArmorGroup = new GroupProperty("Auto Armor", "Automatically puts on armor.", this);
    private final EnumProperty<ActionMode> autoArmorMode = new EnumProperty<>("Mode", "Auto armor mode", ActionMode.ALWAYS);
    private final NumberProperty autoArmorDelay = new NumberProperty("Delay", "Delay between putting armor on", 5, 0, 20, 1, NumberProperty.FORMAT_TICKS);
    private final BooleanProperty autoArmorDrop = new BooleanProperty("Drop", "Drop armor that is taken off.", false);

    private final GroupProperty dropsGroup = new GroupProperty("Drops", "Item dropping", this);
    private final BooleanProperty dropSwords = new BooleanProperty("Swords", "Drops swords.", true);
    private final BooleanProperty dropBows = new BooleanProperty("Bows", "Drops bows.", true);
    private final BooleanProperty dropArmor = new BooleanProperty("Armor", "Drops armor.", true);
    private final BooleanProperty dropTools = new BooleanProperty("Tools", "Drops tools.", false);
    private final BooleanProperty dropUtil = new BooleanProperty("Util", "Drops utility items.", false);
    private final BooleanProperty dropFood = new BooleanProperty("Food", "Drops food.", false);
    private final BooleanProperty dropBlocks = new BooleanProperty("Blocks", "Drops blocks.", false);
    private final BooleanProperty dropUhc = new BooleanProperty("UHC", "Drop UHC items.", false);
    private final BooleanProperty dropOres = new BooleanProperty("Ores", "Drops ores.", false);
    private final BooleanProperty dropPotions = new BooleanProperty("Potions", "Drops potions.", false);
    private final BooleanProperty dropGarbage = new BooleanProperty("Garbage", "Drops items that are not considered useful.", true);

    private final GroupProperty weaponsGroup = new GroupProperty("Weapons", "Swords, Bows & Armor.", this);
    private final GroupProperty toolsGroup = new GroupProperty("Tools", "Pickaxes, Shovels, etc.", this);
    private final GroupProperty utilsGroup = new GroupProperty("Utility", "Utility items.", this);
    private final GroupProperty uhcGroup = new GroupProperty("UHC", "Items that are useful in UHC.", this);
    private final GroupProperty oresGroup = new GroupProperty("Ores", "Diamonds, Iron, etc.", this);
    private final GroupProperty potsGroup = new GroupProperty("Potions", "Potions.", this);

    private final EnumProperty<ActionMode> swapMode = new EnumProperty<>("Swap Mode", "When to swap items", ActionMode.ALWAYS);
    private final NumberProperty swapDelay = new NumberProperty("Swap Delay", "Delay between swapping items", 5, 0, 20, 1, NumberProperty.FORMAT_TICKS);
    private final EnumProperty<ActionPattern> swapPattern = new EnumProperty<>("Swap Pattern", "Order to swap items.", ActionPattern.PRIORITY);
    private final EnumProperty<ActionMode> dropMode = new EnumProperty<>("Drop Mode", "When to drop items", ActionMode.ALWAYS);
    private final NumberProperty dropDelay = new NumberProperty("Drop Delay", "Delay between dropping items", 2, 0, 20, 1, NumberProperty.FORMAT_TICKS);
    private final EnumProperty<ActionPattern> dropPattern = new EnumProperty<>("Drop Pattern", "Order to drop items.", ActionPattern.PRIORITY);
    private final NumberProperty minHoldTime = new NumberProperty("Min Hold Time", "Time an item must be in your inventory before doing actions on it.", 6, 0, 20, 1, NumberProperty.FORMAT_TICKS);

    private final EnumProperty<PotionSorting> potionSorting = new EnumProperty<>("Sorting", "How to sort the potions.", PotionSorting.AMPLIFIER);

    private final Map<Integer, ItemSlot> itemSlots = new HashMap<>();

    private final List<ItemTracker> trackers = new ArrayList<>();
    private final List<ItemSwapper> swappers = new ArrayList<>();
    private final List<ItemSwapper> armorSwappers = new ArrayList<>();

    private final List<Integer> garbageSlots = new ArrayList<>();

    private final Map<Integer, ItemStack> currentTickItems = new HashMap<>();
    private final Map<Integer, ItemStack> prevTickItems = new HashMap<>();
    private final Map<Integer, Integer> itemTimeInSlot = new HashMap<>();

    private final List<SwapAction> autoArmorToPerform = new ArrayList<>();
    private final List<SwapAction> swapsToPerform = new ArrayList<>();
    private final List<Integer> dropsToPerform = new ArrayList<>();

    private final List<Action> inventoryActions = new ArrayList<>();

    private int delay = 0;
    private boolean skip = false;

    public InventoryManager() {
        super();

        this.slotsGroup.addProperties(this.swordSlot,
                this.bowSlot,
                this.fishingRodSlot,
                this.enderPearlSlot,
                this.goldenAppleSlot,
                this.blocksSlot,
                this.pickaxeSlot,
                this.axeSlot,
                this.shovelSlot);

        this.dropsGroup.addProperties(this.dropSwords,
                this.dropBows,
                this.dropArmor,
                this.dropTools,
                this.dropUtil,
                this.dropFood,
                this.dropBlocks,
                this.dropUhc,
                this.dropOres,
                this.dropPotions,
                this.dropGarbage);

        this.autoArmorGroup.addProperties(this.autoArmorMode, this.autoArmorDelay, this.autoArmorDrop);

        this.potsGroup.addProperties(this.potionSorting);

        this.itemProperties.add(new ItemProperty(ItemType.SWORDS, 1, this.dropSwords, this.weaponsGroup, this.swordSlot::getInt));
        this.itemProperties.add(new ItemProperty(ItemType.BOWS, 1, this.dropBows, this.weaponsGroup, this.bowSlot::getInt));
        this.itemProperties.add(new ItemProperty(ItemType.HELMETS, 1, this.dropArmor, this.weaponsGroup, () -> 5));
        this.itemProperties.add(new ItemProperty(ItemType.CHESTPLATES, 1, this.dropArmor, this.weaponsGroup, () -> 6));
        this.itemProperties.add(new ItemProperty(ItemType.LEGGINGS, 1, this.dropArmor, this.weaponsGroup, () -> 7));
        this.itemProperties.add(new ItemProperty(ItemType.BOOTS, 1, this.dropArmor, this.weaponsGroup, () -> 8));

        this.itemProperties.add(new ItemProperty(ItemType.PICKAXES, 1, this.dropTools, this.toolsGroup, this.pickaxeSlot::getInt));
        this.itemProperties.add(new ItemProperty(ItemType.AXES, 1, this.dropTools, this.toolsGroup, this.axeSlot::getInt));
        this.itemProperties.add(new ItemProperty(ItemType.SHOVELS, 1, this.dropTools, this.toolsGroup, this.shovelSlot::getInt));
        this.itemProperties.add(new ItemProperty(ItemType.SHEARS, 1, this.dropTools, this.toolsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.FLINT_AND_STEEL, 1, this.dropTools, this.toolsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.FISHING_ROD, 1, this.dropTools, this.toolsGroup, this.fishingRodSlot::getInt));

        this.itemProperties.add(new ItemProperty(ItemType.BLOCKS, 3, this.dropBlocks, this.utilsGroup, this.blocksSlot::getInt));
        this.itemProperties.add(new ItemProperty(ItemType.GOLDEN_APPLES, 1, this.dropFood, this.utilsGroup, this.goldenAppleSlot::getInt));
        this.itemProperties.add(new ItemProperty(ItemType.ENDER_PEARLS, 1, this.dropUtil, this.utilsGroup, this.enderPearlSlot::getInt));
        this.itemProperties.add(new ItemProperty(ItemType.BOATS, 1, this.dropUtil, this.utilsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.TNT, 1, this.dropUtil, this.utilsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.ARROWS, 1, this.dropUtil, this.utilsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.EGGS_SNOWBALLS, 1, this.dropUtil, this.utilsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.FOOD, 1, this.dropFood, this.utilsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.WATER_BUCKETS, 1, this.dropUtil, this.utilsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.LAVA_BUCKETS, 1, this.dropUtil, this.utilsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.MILK_BUCKETS, 1, this.dropUtil, this.utilsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.COBWEBS, 1, this.dropUtil, this.utilsGroup));

        this.itemProperties.add(new ItemProperty(ItemType.APPLES, 1, this.dropUhc, this.uhcGroup));
        this.itemProperties.add(new ItemProperty(ItemType.CRAFTING_TABLES, 1, this.dropUhc, this.uhcGroup));
        this.itemProperties.add(new ItemProperty(ItemType.STICKS, 1, this.dropUhc, this.uhcGroup));
        this.itemProperties.add(new ItemProperty(ItemType.ANVILS, 1, this.dropUhc, this.uhcGroup));
        this.itemProperties.add(new ItemProperty(ItemType.ENCHANTING_TABLES, 1, this.dropUhc, this.uhcGroup));
        this.itemProperties.add(new ItemProperty(ItemType.FURNACES, 1, this.dropUhc, this.uhcGroup));
        this.itemProperties.add(new ItemProperty(ItemType.EXP_BOTTLES, 1, this.dropUhc, this.uhcGroup));
        this.itemProperties.add(new ItemProperty(ItemType.GOLD_BLOCKS, 1, this.dropUhc, this.uhcGroup));
        this.itemProperties.add(new ItemProperty(ItemType.CHESTS, 1, this.dropUhc, this.uhcGroup));
        this.itemProperties.add(new ItemProperty(ItemType.STRING, 1, this.dropUhc, this.uhcGroup));

        this.itemProperties.add(new ItemProperty(ItemType.DIAMONDS, 1, this.dropOres, this.oresGroup));
        this.itemProperties.add(new ItemProperty(ItemType.GOLD, 1, this.dropOres, this.oresGroup));
        this.itemProperties.add(new ItemProperty(ItemType.GOLD_ORE, 1, this.dropOres, this.oresGroup));
        this.itemProperties.add(new ItemProperty(ItemType.IRON, 1, this.dropOres, this.oresGroup));
        this.itemProperties.add(new ItemProperty(ItemType.IRON_ORE, 1, this.dropOres, this.oresGroup));
        this.itemProperties.add(new ItemProperty(ItemType.COAL, 1, this.dropOres, this.oresGroup));
        this.itemProperties.add(new ItemProperty(ItemType.REDSTONE, 1, this.dropOres, this.oresGroup));
        this.itemProperties.add(new ItemProperty(ItemType.LAPIS, 1, this.dropOres, this.oresGroup));
        this.itemProperties.add(new ItemProperty(ItemType.EMERALDS, 1, this.dropOres, this.oresGroup));

        this.itemProperties.add(new ItemProperty(ItemType.SPEED, 3, this.dropPotions, this.potsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.REGENERATION, 3, this.dropPotions, this.potsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.HEALING, 3, this.dropPotions, this.potsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.JUMP_BOOST, 0, this.dropPotions, this.potsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.FIRE_RESISTANCE, 3, this.dropPotions, this.potsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.RESISTANCE, 3, this.dropPotions, this.potsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.INVISIBILITY, 0, this.dropPotions, this.potsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.ABSORPTION, 1, this.dropPotions, this.potsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.WEAKNESS, 0, this.dropPotions, this.potsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.SLOWNESS, 0, this.dropPotions, this.potsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.POISON, 0, this.dropPotions, this.potsGroup));
        this.itemProperties.add(new ItemProperty(ItemType.HARMING, 0, this.dropPotions, this.potsGroup));

        this.addProperties(this.slotsGroup,
                this.dropsGroup,
                this.autoArmorGroup,
                this.weaponsGroup,
                this.toolsGroup,
                this.utilsGroup,
                this.uhcGroup,
                this.oresGroup,
                this.potsGroup,
                this.swapMode,
                this.swapDelay,
                this.swapPattern,
                this.dropMode,
                this.dropDelay,
                this.dropPattern,
                this.minHoldTime);

        for (ItemProperty itemProperty : this.itemProperties) {
            ItemTracker tracker = new ItemTracker(itemProperty.type::check, itemProperty.type::getScore, itemProperty::getInt, itemProperty.masterProperty::get);
            this.trackers.add(tracker);
            if (itemProperty.targetSlot != null) {
                if (itemProperty.type == ItemType.HELMETS || itemProperty.type == ItemType.CHESTPLATES || itemProperty.type == ItemType.LEGGINGS || itemProperty.type == ItemType.BOOTS) {
                    this.armorSwappers.add(new ItemSwapper(tracker, itemProperty.targetSlot));
                } else {
                    this.swappers.add(new ItemSwapper(tracker, itemProperty.targetSlot));
                }
            }
        }

    }

    @EventLink(EventPriorities.VERY_LOW)
    public final Listener<EventWindowClick> onWindowClick = event -> {
          if (event.windowId != -1) {
              this.skip = true;
          }
    };

    @EventLink
    public final Listener<EventUpdate> onUpdate = _ -> {

        if (mc.currentScreen instanceof GuiContainerCreative || mc.currentScreen instanceof GuiCustomCreative) {
            return;
        }

        if (this.skip) {
            this.skip = false;
            return;
        }

        if (this.canUpdate()) {
            this.update();

            if (this.canInteract(this.swapMode.get())) {
                this.doAutoArmor();
            }

            if (this.canInteract(this.swapMode.get())) {
                this.doSwaps();
            }

            if (this.canInteract(this.dropMode.get())) {
                this.doDrops();
            }

            this.reset();
        } else if (this.canInteract(this.autoArmorMode.get())) {
            this.doInventoryQueueActions();
        }

        if (this.delay > 0) {
            this.delay--;
        }
    };

    private void update() {

        for (int slot = InventoryUtil.INCLUDE_ARMOR_BEGIN; slot < InventoryUtil.END; slot++) {
            ItemStack itemStack = InventoryUtil.getStack(slot);

            if (itemStack != null) {
                boolean isGarbage = true;

                ItemSlot itemSlot = new ItemSlot(itemStack, slot);
                this.itemSlots.put(slot, itemSlot);
                for (ItemTracker tracker : this.trackers) {
                    if (tracker.addItemIfChecked(itemSlot)) {
                        isGarbage = false;
                    }
                }

                if (isGarbage) {
                    this.garbageSlots.add(slot);
                }

            }

            this.prevTickItems.put(slot, this.currentTickItems.get(slot));

            if (itemStack != null) {
                this.currentTickItems.put(slot, itemStack);
            } else {
                this.currentTickItems.remove(slot);
            }

            ItemStack prevTickItem = this.prevTickItems.get(slot);
            ItemStack currentTickItem = this.currentTickItems.get(slot);
            if (!this.itemTimeInSlot.containsKey(slot) ||
                    (prevTickItem == null && currentTickItem != null) ||
                    (prevTickItem != null && currentTickItem == null) ||
                    !ItemStack.areItemStacksEqual(prevTickItem, currentTickItem)) {
                this.itemTimeInSlot.put(slot, 0);
            } else {
                this.itemTimeInSlot.put(slot, this.itemTimeInSlot.getOrDefault(slot, 0) + 1);
            }
        }

        this.computeAutoArmor();
        this.computeSwaps();
        this.computeDrops();
    }

    private void computeAutoArmor() {
        this.autoArmorToPerform.clear();

        if (!this.canInteract(this.autoArmorMode.get())) {
            return;
        }

        for (ItemSwapper swapper : this.armorSwappers) {
            ItemTracker tracker = swapper.tracker();
            int targetSlot = swapper.targetSlot().get();
            if (tracker.getAmountOfItems() > 0 && tracker.getLast().slot != targetSlot) {
                ItemSlot bestItem = tracker.getLast();
                ItemSlot currentItem = this.itemSlots.get(targetSlot);
                int timeInSlot = this.itemTimeInSlot.getOrDefault(targetSlot, -1);
                if (timeInSlot >= this.minHoldTime.getInt() &&
                        (currentItem == null || !tracker.check(currentItem.stack) || tracker.getScore(bestItem.stack) > tracker.getScore(currentItem.stack))) {
                    this.autoArmorToPerform.add(new SwapAction(bestItem.slot, targetSlot));
                }
            }
        }

    }

    private void computeSwaps() {
        this.swapsToPerform.clear();

        if (!this.canInteract(this.swapMode.get())) {
            return;
        }

        for (ItemSwapper swapper : this.swappers) {
            ItemTracker tracker = swapper.tracker();
            int targetSlot = swapper.targetSlot().get() - 1 + InventoryUtil.HOTBAR_BEGIN;
            if (targetSlot >= InventoryUtil.HOTBAR_BEGIN &&
                    targetSlot < InventoryUtil.END &&
                    tracker.getAmountOfItems() > 0 &&
                    tracker.getLast().slot != targetSlot) {
                ItemSlot bestItem = tracker.getLast();
                ItemSlot currentItem = this.itemSlots.get(targetSlot);
                int timeInSlot = this.itemTimeInSlot.getOrDefault(targetSlot, -1);
                if (timeInSlot >= this.minHoldTime.getInt() &&
                        (currentItem == null || !tracker.check(currentItem.stack) || tracker.getScore(bestItem.stack) > tracker.getScore(currentItem.stack))) {
                    this.swapsToPerform.add(new SwapAction(bestItem.slot, targetSlot));
                }
            }
        }

        this.sortActions(this.swapsToPerform, this.swapPattern.get(), Comparator.comparingInt(swap -> swap.dstSlot));
    }

    private void computeDrops() {
        this.dropsToPerform.clear();

        if (!this.canInteract(this.dropMode.get())) {
            return;
        }

        for (ItemTracker tracker : this.trackers) {
            if (tracker.getAmountToKeep() != 0 && tracker.dropEnabled()) {
                for (int i = 0; i < tracker.getItems().size() - tracker.getAmountToKeep(); i++) {
                    ItemSlot itemSlot = tracker.get(i);
                    int timeInSlot = this.itemTimeInSlot.getOrDefault(itemSlot.slot, -1);
                    if (timeInSlot >= this.minHoldTime.getInt()) {
                        this.dropsToPerform.add(itemSlot.slot);
                    }
                }
            }
        }

        if (this.dropGarbage.get()) {
            for (int slot : this.garbageSlots) {
                int timeInSlot = this.itemTimeInSlot.getOrDefault(slot, -1);
                if (timeInSlot >= this.minHoldTime.getInt()) {
                    this.dropsToPerform.add(slot);
                }
            }
        }

        this.sortActions(this.dropsToPerform, this.dropPattern.get(), Integer::compare);
    }

    private void doAutoArmor() {
        while (!this.autoArmorToPerform.isEmpty()) {
            SwapAction action = this.autoArmorToPerform.removeFirst();
            this.queueAutoArmorSwap(action.srcSlot, action.dstSlot);

            this.delay = this.autoArmorDelay.getInt();
            if (this.delay > 0) {
                break;
            }

        }
    }

    private void doSwaps() {

        while (!this.swapsToPerform.isEmpty()) {
            SwapAction action = this.swapsToPerform.removeFirst();
            this.swap(action.srcSlot, action.dstSlot);

            this.delay = this.swapDelay.getInt();
            if (this.delay > 0) {
                break;
            }

        }
    }

    private void doDrops() {

        while (!this.dropsToPerform.isEmpty()) {
            int slot = this.dropsToPerform.removeFirst();
            this.drop(slot);

            this.delay = this.swapDelay.getInt();
            if (this.delay > 0) {
                break;
            }

        }
    }

    private void reset() {
        for (ItemTracker tracker : this.trackers) {
            tracker.clear();
        }
        this.itemSlots.clear();
        this.garbageSlots.clear();
    }

    private boolean canUpdate() {
        return this.inventoryActions.isEmpty() && this.delay <= 0;
    }

    private boolean canInteract(ActionMode mode) {
        return this.delay <= 0 && switch (mode) {
            case ALWAYS -> true;
            case NOT_MOVING -> !MoveUtil.isMoving();
            case INVENTORY -> mc.currentScreen instanceof GuiInventory;
            case NONE -> false;
        };
    }

    private void swap(int srcSlot, int dstSlot) {
        if (srcSlot != dstSlot && dstSlot >= InventoryUtil.HOTBAR_BEGIN && dstSlot < InventoryUtil.END) {
            InventoryUtil.windowClick(0, srcSlot, dstSlot - InventoryUtil.HOTBAR_BEGIN, InventoryUtil.SWAP);
        } else {
            Notification.send("Inventory Manager", String.format("Swap called with unexpected args: %d -> %d", srcSlot, dstSlot), NotificationType.ERROR, 5000);
            return;
        }

        ItemSlot srcItemSlot = this.itemSlots.get(srcSlot);
        ItemSlot dstItemSlot = this.itemSlots.get(dstSlot);

        if (srcItemSlot != null) {
            srcItemSlot.slot = srcSlot;
        }

        if (dstItemSlot != null) {
            dstItemSlot.slot = dstSlot;
        }

        this.itemSlots.put(srcSlot, srcItemSlot);
        this.itemSlots.put(dstSlot, dstItemSlot);

        this.itemTimeInSlot.put(srcSlot, 0);
        this.itemTimeInSlot.put(dstSlot, 0);

    }

    private void queueAutoArmorSwap(int srcSlot, int dstSlot) {
        int delay = this.autoArmorDelay.getInt();

        if (InventoryUtil.getStack(dstSlot) == null) {
            InventoryUtil.windowClick(0, srcSlot, 0, InventoryUtil.QUICK_MOVE);
        } else {
            InventoryUtil.windowClick(0, srcSlot, 0, InventoryUtil.PICKUP);
            if (delay == 0) {
                InventoryUtil.windowClick(0, dstSlot, 0, InventoryUtil.PICKUP);
                if (this.autoArmorDrop.get()) {
                    InventoryUtil.windowClick(0, -999, 0, InventoryUtil.PICKUP);
                } else {
                    InventoryUtil.windowClick(0, srcSlot, 0, InventoryUtil.PICKUP);
                }
            } else {
                this.inventoryActions.add(new Action(0, dstSlot, 0, InventoryUtil.PICKUP, delay));
                if (this.autoArmorDrop.get()) {
                    this.inventoryActions.add(new Action(0, -999, 0, InventoryUtil.PICKUP, delay));
                } else {
                    this.inventoryActions.add(new Action(0, srcSlot, 0, InventoryUtil.PICKUP, delay));
                }
            }
        }

        ItemSlot srcItemSlot = this.itemSlots.get(srcSlot);
        ItemSlot dstItemSlot = this.itemSlots.get(dstSlot);

        if (srcItemSlot != null) {
            srcItemSlot.slot = srcSlot;
        }

        if (dstItemSlot != null) {
            dstItemSlot.slot = dstSlot;
        }

        this.itemSlots.put(srcSlot, srcItemSlot);
        this.itemSlots.put(dstSlot, dstItemSlot);

        this.itemTimeInSlot.put(srcSlot, 0);
        this.itemTimeInSlot.put(dstSlot, 0);
    }

    private void drop(int slot) {
        InventoryUtil.windowClick(0, slot, 1, InventoryUtil.DROP);
        this.itemSlots.put(slot, null);
        this.itemTimeInSlot.put(slot, 0);
    }

    private boolean doInventoryQueueActions() {
        boolean didAction = false;
        while (!this.inventoryActions.isEmpty()) {
            Action action = this.inventoryActions.removeFirst();
            InventoryUtil.windowClick(action.windowId, action.slot, action.button, action.mode);
            didAction = true;
            this.delay = action.delay;
            if (this.delay > 0) {
                break;
            }
        }

        return didAction;
    }

    private <T> void sortActions(List<T> actions, ActionPattern mode, Comparator<T> comparator) {
        switch (mode) {
            case PRIORITY -> { /* Does nothing */ }
            case REVERSE_PRIORITY -> Collections.reverse(actions);
            case SEQUENTIAL -> actions.sort(comparator);
            case REVERSE -> actions.sort(comparator.reversed());
            case RANDOM -> Collections.shuffle(actions);
        }
    }

    public static class ItemProperty extends NumberProperty {

        public final ItemType type;
        public final BooleanProperty masterProperty;
        public final Supplier<Integer> targetSlot;

        public ItemProperty(ItemType type, int defaultAmount, BooleanProperty masterProperty, GroupProperty group, Supplier<Integer> targetSlot) {
            super(EnumProperty.toDisplay(type), "Amount of " + EnumProperty.toDisplay(type) + " to keep", defaultAmount, 0, 5, 1, FORMAT_STACKS);
            this.type = type;
            this.masterProperty = masterProperty;
            this.targetSlot = targetSlot;
            group.addProperties(this);
        }

        public ItemProperty(ItemType type, int defaultAmount, BooleanProperty masterProperty, GroupProperty group) {
            this(type, defaultAmount, masterProperty, group, null);
        }
    }

    public enum ItemType {

        // Weapons
        SWORDS(stack -> stack.getItem() instanceof ItemSword, ItemScoreCalculator.SWORD),
        BOWS(Items.bow, ItemScoreCalculator.BOW),
        HELMETS(stack -> ItemUtil.isArmorPiece(stack, 0), ItemScoreCalculator.HELMET),
        CHESTPLATES(stack -> ItemUtil.isArmorPiece(stack, 1), ItemScoreCalculator.CHESTPLATE),
        LEGGINGS(stack -> ItemUtil.isArmorPiece(stack, 2), ItemScoreCalculator.LEGGINGS),
        BOOTS(stack -> ItemUtil.isArmorPiece(stack, 3), ItemScoreCalculator.BOOTS),

        // Tools
        PICKAXES(stack -> stack.getItem() instanceof ItemPickaxe, ItemScoreCalculator.PICKAXE),
        AXES(stack -> stack.getItem() instanceof ItemAxe, ItemScoreCalculator.AXE),
        SHOVELS(stack -> stack.getItem() instanceof ItemSpade, ItemScoreCalculator.SHOVEL),
        SHEARS(Items.shears, ItemScoreCalculator.DURABILITY),
        FLINT_AND_STEEL(Items.flint_and_steel, ItemScoreCalculator.DURABILITY),
        FISHING_ROD(Items.fishing_rod, ItemScoreCalculator.FISHING_ROD),

        // Utility
        BLOCKS(ItemType::isBlockStack, ItemScoreCalculator.SIZE),
        GOLDEN_APPLES(Items.golden_apple, stack -> stack.stackSize + (stack.getMetadata() == 1 ? 65 : 0)),
        ENDER_PEARLS(Items.ender_pearl),
        BOATS(Items.boat, ItemScoreCalculator.NONE),
        @DisplayName("TNT") TNT(Blocks.tnt),
        ARROWS(Items.arrow),
        EGGS_SNOWBALLS(stack -> stack.getItem() == Items.egg || stack.getItem() == Items.snowball, ItemScoreCalculator.SIZE),
        FOOD(ItemType::isFoodStack, ItemScoreCalculator.SIZE),
        WATER_BUCKETS(Items.water_bucket, ItemScoreCalculator.NONE),
        LAVA_BUCKETS(Items.lava_bucket, ItemScoreCalculator.NONE),
        MILK_BUCKETS(Items.milk_bucket, ItemScoreCalculator.NONE),
        SAND_GRAVEL(stack -> ItemUtil.isBlock(stack, Blocks.sand) || ItemUtil.isBlock(stack, Blocks.gravel), ItemScoreCalculator.SIZE),
        COBWEBS(Blocks.web),

        // UHC
        APPLES(Items.apple),
        CRAFTING_TABLES(Blocks.crafting_table),
        STICKS(Items.stick),
        ANVILS(Blocks.anvil),
        ENCHANTING_TABLES(Blocks.enchanting_table),
        FURNACES(Blocks.furnace),
        @DisplayName("EXP Bottles") EXP_BOTTLES(Items.experience_bottle),
        GOLD_BLOCKS(Blocks.gold_block),
        CHESTS(Blocks.chest),
        STRING(Items.string),

        // Ores
        DIAMONDS(Items.diamond),
        GOLD(Items.gold_ingot),
        GOLD_ORE(Blocks.gold_ore),
        IRON(Items.iron_ingot),
        IRON_ORE(Blocks.iron_ore),
        COAL(Items.coal),
        REDSTONE(Items.redstone),
        LAPIS(stack -> stack.getItem() == Items.dye && stack.getMetadata() == 4, ItemScoreCalculator.DURABILITY),
        EMERALDS(Items.emerald),

        // Potions
        SPEED(stack -> ItemUtil.isPotion(stack, Potion.moveSpeed) && !ItemUtil.isPotion(stack, Potion.jump), ItemScoreCalculator.NONE),
        STRENGTH(Potion.damageBoost),
        REGENERATION(Potion.regeneration),
        HEALING(Potion.heal),
        JUMP_BOOST(Potion.jump),
        FIRE_RESISTANCE(Potion.fireResistance),
        RESISTANCE(Potion.resistance),
        INVISIBILITY(Potion.invisibility),
        ABSORPTION(Potion.absorption),
        WEAKNESS(Potion.weakness),
        SLOWNESS(Potion.moveSlowdown),
        POISON(Potion.poison),
        HARMING(Potion.harm),

        ;

        private final ItemChecker checker;
        private final ItemScoreCalculator score;

        ItemType(Item item) {
            this.checker = stack -> stack.getItem() == item;
            this.score = ItemScoreCalculator.SIZE;
        }

        ItemType(Block block) {
            this.checker = stack -> stack.getItem() instanceof ItemBlock itemBlock && itemBlock.getBlock() == block;
            this.score = ItemScoreCalculator.SIZE;
        }

        ItemType(Item item, ItemScoreCalculator score) {
            this.checker = stack -> stack.getItem() == item;
            this.score = score;
        }

        ItemType(Potion potion) {
            this.checker = stack -> ItemUtil.isPotion(stack, potion);
            this.score = stack -> switch (Nonsense.module(InventoryManager.class).potionSorting.get()) {
                case AMPLIFIER -> ItemUtil.potionAmplifier(stack, potion);
                case DURATION -> ItemUtil.potionDuration(stack, potion);
            };
        }

        ItemType(ItemChecker checker, ItemScoreCalculator score) {
            this.checker = checker;
            this.score = score;
        }

        public boolean check(ItemStack stack) {
            if (stack == null) {
                return false;
            }
            return this.checker.check(stack);
        }

        public float getScore(ItemStack stack) {
            if (stack == null) {
                return -1.0F;
            }
            return this.score.getScore(stack);
        }

        public static boolean isBlockStack(ItemStack stack) {
            if (stack.getItem() instanceof ItemBlock itemBlock) {
                Block block = itemBlock.getBlock();
                if (!block.isNormalCube() || Scaffold.BAD_BLOCKS.contains(block)) {
                    return false;
                }

                for (ItemType type : values()) {
                    if (type == BLOCKS) {
                        continue;
                    }

                    if (type.check(stack)) {
                        return false;
                    }
                }

                return true;
            }

            return false;
        }

        public static boolean isFoodStack(ItemStack stack) {
            if (stack.getItem() instanceof ItemFood food) {
                if (food.getPotionId() > 0) {
                    return false;
                }
                for (ItemType type : values()) {
                    if (type == FOOD) {
                        continue;
                    }

                    if (type.check(stack)) {
                        return false;
                    }
                }

                return true;
            }

            return false;
        }

    }

    public record Action(int windowId, int slot, int button, int mode, int delay) { }

    public record SwapAction(int srcSlot, int dstSlot) { }

    public enum ActionMode {
        ALWAYS,
        NOT_MOVING,
        INVENTORY,
        NONE
    }

    public enum ActionPattern {
        PRIORITY,
        REVERSE_PRIORITY,
        SEQUENTIAL,
        REVERSE,
        RANDOM
    }

    public enum PotionSorting {
        AMPLIFIER,
        DURATION
    }


}
