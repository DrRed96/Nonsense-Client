package wtf.bhopper.nonsense.util.minecraft.inventory;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.*;

@FunctionalInterface
public interface ItemScoreSupplier {

    float getScore(ItemStack stack);

    ItemScoreSupplier NONE = _ -> 0.0F;
    ItemScoreSupplier SIZE = stack -> stack.stackSize;

    ItemScoreSupplier DURABILITY = stack -> {
        if (!stack.getItem().isDamageable()) {
            return Float.MAX_VALUE;
        }

        return (stack.getMaxDamage() - stack.getItemDamage()) * (EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking, stack) + 1);

    };

    ItemScoreSupplier SWORD = stack -> {
        if (!(stack.getItem() instanceof ItemSword sword)) {
            return 0.0F;
        }

        float score = sword.getDamageVsEntity();

        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness, stack) * 1.25F;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect, stack) * 0.1F;

        return score;
    };

    ItemScoreSupplier BOW = stack -> {
        if (stack.getItem() != Items.bow) {
            return 0.0F;
        }

        float score = 0.0F;

        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.power, stack) * 1.25F;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.punch, stack) * 1.2F;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.flame, stack) * 1.1F;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity, stack);

        return score;
    };

    ItemScoreSupplier PICKAXE = stack -> {
        if (!(stack.getItem() instanceof ItemPickaxe pickaxe)) {
            return 0.0F;
        }

        float score = pickaxe.getToolMaterial().getEfficiencyOnProperMaterial();

        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency, stack) * 1.25F;

        return score;

    };

    ItemScoreSupplier AXE = stack -> {
        if (!(stack.getItem() instanceof ItemAxe axe)) {
            return 0.0F;
        }

        float score = axe.getToolMaterial().getEfficiencyOnProperMaterial();

        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency, stack) * 1.25F;

        return score;

    };

    ItemScoreSupplier SHOVEL = stack -> {
        if (!(stack.getItem() instanceof ItemSpade shovel)) {
            return 0.0F;
        }

        float score = shovel.getToolMaterial().getEfficiencyOnProperMaterial();

        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency, stack) * 1.25F;

        return score;
    };

}
