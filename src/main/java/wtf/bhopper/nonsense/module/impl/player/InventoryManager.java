package wtf.bhopper.nonsense.module.impl.player;

import net.minecraft.block.BlockFalling;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.util.minecraft.inventory.ItemChecker;
import wtf.bhopper.nonsense.util.minecraft.inventory.ItemScoreSupplier;

@ModuleInfo(name = "Inventory Manager",
        description = "Manages your inventory.",
        category = ModuleCategory.PLAYER)
public class InventoryManager extends Module {



    public enum ItemType {

        // Weapons
        SWORD(stack -> stack.getItem() instanceof ItemSword, ItemScoreSupplier.SWORD),

        // Tools
        PICKAXE(stack -> stack.getItem() instanceof ItemPickaxe, ItemScoreSupplier.PICKAXE),
        AXE(stack -> stack.getItem() instanceof ItemAxe, ItemScoreSupplier.AXE),
        SHOVEL(stack -> stack.getItem() instanceof ItemSpade, ItemScoreSupplier.SHOVEL),
        FLINT_AND_STEEL(stack -> stack.getItem() == Items.flint_and_steel, ItemScoreSupplier.DURABILITY),

        // Utility
        BLOCKS(stack -> stack.getItem() instanceof ItemBlock block && block.getBlock().isNormalCube() && !(block.getBlock() instanceof BlockFalling), ItemScoreSupplier.SIZE),
        GOLDEN_APPLE(stack -> stack.getItem() == Items.golden_apple, ItemScoreSupplier.SIZE);

        public final ItemChecker checker;
        public final ItemScoreSupplier score;

        ItemType(ItemChecker checker, ItemScoreSupplier score) {
            this.checker = checker;
            this.score = score;
        }

    }


}
