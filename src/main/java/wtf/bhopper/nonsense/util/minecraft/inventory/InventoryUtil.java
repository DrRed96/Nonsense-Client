package wtf.bhopper.nonsense.util.minecraft.inventory;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import wtf.bhopper.nonsense.util.minecraft.IMinecraft;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;

public class InventoryUtil implements IMinecraft {

    /** Inventory slot constants */
    public static final int
            INCLUDE_ARMOR_BEGIN = 5,
            EXCLUDE_ARMOR_BEGIN = 9,
            HOTBAR_BEGIN = 36,
            END = 45;

    /** Inventory actions */
    public static final int
            PICKUP = 0,
            QUICK_MOVE = 1,
            SWAP = 2,
            CLONE = 3,
            DROP = 4,
            QUICK_CRAFT = 5,
            PICKUP_ALL = 6;

    public static ItemStack getStack(int slot) {
        return mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
    }

    public static void windowClick(int windowId, int slot, int button, int mode) {
        if (slot == -1) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        }
        mc.playerController.windowClick(windowId, slot, button, mode, mc.thePlayer);
    }

    public static boolean placeStackInInventory(ItemStack stack) {
        for (int i = 0; i < 36; i++) {
            int slot = i < 9 ? i + 36 : i;
            if (mc.thePlayer.inventory.getStackInSlot(i) == null) {
                mc.thePlayer.inventory.setInventorySlotContents(i, stack.copy());
                PacketUtil.send(new C10PacketCreativeInventoryAction(slot, stack));
                return true;
            }
        }

        return false;
    }

    public static int getCurrentWindowId() {
        return mc.thePlayer.openContainer.windowId;
    }

    public static Container getOpenContainer() {
        return mc.thePlayer.openContainer;
    }

    public static int convertToProperSlot(int slot) {
        Container container = mc.thePlayer.openContainer;
        int windowId = container.windowId;

        if (windowId == 0) {
            return slot;
        }

        if (slot < EXCLUDE_ARMOR_BEGIN) {
            return -1;
        }

        int length = container.inventorySlots.size();
        int numberOfSlots = length - 36;
        return slot - EXCLUDE_ARMOR_BEGIN + numberOfSlots;
    }

}
