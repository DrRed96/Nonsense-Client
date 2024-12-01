package wtf.bhopper.nonsense.util.minecraft;

import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;

public class InventoryUtil implements MinecraftInstance {

    // Constants for slots in the inventory (https://wiki.vg/File:Inventory-slots.png)
    public static final int INCLUDE_ARMOR_BEGIN = 5;
    public static final int EXCLUDE_ARMOR_BEGIN = 9;
    public static final int HOTBAR_BEGIN = 36;
    public static final int END = 45;

    public static int serverItem = 0;

    public static int currentItem() {
        return serverItem;
    }

    public static ItemStack getStackInSlot(int slot) {
        return mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
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

}
