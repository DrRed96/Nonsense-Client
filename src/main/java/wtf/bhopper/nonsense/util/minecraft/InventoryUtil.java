package wtf.bhopper.nonsense.util.minecraft;

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

}
