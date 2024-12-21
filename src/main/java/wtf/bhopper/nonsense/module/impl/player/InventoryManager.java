package wtf.bhopper.nonsense.module.impl.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.impl.movement.Scaffold;
import wtf.bhopper.nonsense.util.minecraft.inventory.ItemChecker;
import wtf.bhopper.nonsense.util.minecraft.inventory.ItemScoreSupplier;

@ModuleInfo(name = "Inventory Manager",
        description = "Manages your inventory.",
        category = ModuleCategory.PLAYER)
public class InventoryManager extends Module {

    public InventoryManager() {

    }

    public enum ItemType {

        // Weapons
        SWORD(stack -> stack.getItem() instanceof ItemSword, ItemScoreSupplier.SWORD),
        BOW(Items.bow, ItemScoreSupplier.BOW),

        // Tools
        PICKAXE(stack -> stack.getItem() instanceof ItemPickaxe, ItemScoreSupplier.PICKAXE),
        AXE(stack -> stack.getItem() instanceof ItemAxe, ItemScoreSupplier.AXE),
        SHOVEL(stack -> stack.getItem() instanceof ItemSpade, ItemScoreSupplier.SHOVEL),
        FLINT_AND_STEEL(Items.flint_and_steel, ItemScoreSupplier.DURABILITY),

        // Utility
        BLOCKS(stack -> stack.getItem() instanceof ItemBlock block && block.getBlock().isNormalCube() && Scaffold.BAD_BLOCKS.contains(block.getBlock()) && !(block.getBlock() instanceof BlockFalling), ItemScoreSupplier.SIZE),
        GOLDEN_APPLE(Items.golden_apple),
        ENDER_PEARLS(Items.ender_pearl),
        BOATS(Items.boat, ItemScoreSupplier.NONE),
        TNT(Blocks.tnt),
        ARROWS(Items.arrow),
        EGGS_SNOWBALLS(stack -> stack.getItem() == Items.egg || stack.getItem() == Items.snowball, ItemScoreSupplier.SIZE),
        FOOD(stack -> stack.getItem() instanceof ItemFood, ItemScoreSupplier.SIZE),
        WATER_BUCKETS(Items.water_bucket, ItemScoreSupplier.NONE),
        LAVA_BUCKETS(Items.lava_bucket, ItemScoreSupplier.NONE),
        MILK_BUCKETS(Items.milk_bucket, ItemScoreSupplier.NONE),
        COBWEBS(Blocks.web)
        ;

        public final ItemChecker checker;
        public final ItemScoreSupplier score;

        ItemType(Item item) {
            this.checker = stack -> stack.getItem() == item;
            this.score = ItemScoreSupplier.SIZE;
        }

        ItemType(Block block) {
            this.checker = stack -> stack.getItem() instanceof ItemBlock itemBlock && itemBlock.getBlock() == block;
            this.score = ItemScoreSupplier.SIZE;
        }

        ItemType(Item item, ItemScoreSupplier score) {
            this.checker = stack -> stack.getItem() == item;
            this.score = score;
        }

        ItemType(ItemChecker checker, ItemScoreSupplier score) {
            this.checker = checker;
            this.score = score;
        }

    }


}
