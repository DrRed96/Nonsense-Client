package wtf.bhopper.nonsense.util.minecraft.inventory;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import wtf.bhopper.nonsense.util.minecraft.IMinecraft;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;

public class InventoryUtil implements IMinecraft {

    // Constants for slots in the inventory
    public static final int INCLUDE_ARMOR_BEGIN = 5;
    public static final int EXCLUDE_ARMOR_BEGIN = 9;
    public static final int HOTBAR_BEGIN = 36;
    public static final int END = 45;

    public static final int PICKUP = 0;
    public static final int QUICK_MOVE = 1;
    public static final int SWAP = 2;
    public static final int CLONE = 3;
    public static final int DROP = 4;
    public static final int QUICK_CRAFT = 5;
    public static final int PICKUP_ALL = 6;

    public static ItemStack getStack(int slot) {
        return mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
    }

    public static void windowClick(int windowId, int slot, int button, int mode) {
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

    public static int properSlot(int windowId, int slotId) {

        GuiScreen currentScreen = mc.currentScreen;

        if (windowId != 0) {
            if (!(currentScreen instanceof GuiContainer guiContainer)) {
                return -1;
            }

            if (guiContainer.inventorySlots.windowId != windowId) {
                return -1;
            }

            return slotId;
        }

        if (currentScreen instanceof GuiContainer guiContainer) {
            int containerSize = guiContainer.inventorySlots.inventorySlots.size() - 36;

            if (slotId < EXCLUDE_ARMOR_BEGIN) {
                return -1;
            }

            return slotId - EXCLUDE_ARMOR_BEGIN + containerSize;
        }

        return slotId;
    }

}
