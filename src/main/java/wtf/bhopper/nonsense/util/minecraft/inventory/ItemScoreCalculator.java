package wtf.bhopper.nonsense.util.minecraft.inventory;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.*;

@FunctionalInterface
public interface ItemScoreCalculator {

    float getScore(ItemStack stack);

    ItemScoreCalculator NONE = _ -> 0.0F;
    ItemScoreCalculator SIZE = stack -> stack.stackSize;

    ItemScoreCalculator DURABILITY = stack -> {
        if (!stack.getItem().isDamageable()) {
            return Float.MAX_VALUE;
        }

        return (stack.getMaxDamage() - stack.getItemDamage()) * (EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking, stack) + 1);

    };

    ItemScoreCalculator SWORD = stack -> {
        if (!(stack.getItem() instanceof ItemSword sword)) {
            return -1.0F;
        }

        float score = sword.getDamageVsEntity();

        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness, stack) * 1.25F;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect, stack) * 0.1F;

        return score;
    };

    ItemScoreCalculator BOW = stack -> {
        if (stack.getItem() != Items.bow) {
            return -1.0F;
        }

        float score = 0.0F;

        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.power, stack) * 1.25F;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.punch, stack) * 1.2F;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.flame, stack) * 1.1F;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity, stack);

        return score;
    };

    ItemScoreCalculator HELMET = stack -> {

        if (!(stack.getItem() instanceof ItemArmor item) || item.armorType != 0) {
            return -1.0F;
        }

        if (stack.isItemStackDamageable()) {
            int damageLeft = stack.getMaxDamage() - stack.getItemDamage();
            if (damageLeft < 20) {
                return 0.0F;
            }
        }

        float score = item.damageReduceAmount;

        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 2.5F;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) * 0.1F;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.respiration.effectId, stack) * 0.1F;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.aquaAffinity.effectId, stack) * 0.1F;

        return score;
    };

    ItemScoreCalculator CHESTPLATE = stack -> {

        if (!(stack.getItem() instanceof ItemArmor item) || item.armorType != 1) {
            return -1.0F;
        }

        if (stack.isItemStackDamageable()) {
            int damageLeft = stack.getMaxDamage() - stack.getItemDamage();
            if (damageLeft < 20) {
                return 0.0F;
            }
        }

        float score = item.damageReduceAmount;

        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 2.5F;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) * 0.1F;

        return score;
    };

    ItemScoreCalculator LEGGINGS = stack -> {

        if (!(stack.getItem() instanceof ItemArmor item) || item.armorType != 2) {
            return -1.0F;
        }

        if (stack.isItemStackDamageable()) {
            int damageLeft = stack.getMaxDamage() - stack.getItemDamage();
            if (damageLeft < 20) {
                return 0.0F;
            }
        }

        float score = item.damageReduceAmount;

        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 2.5F;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) * 0.1F;

        return score;
    };

    ItemScoreCalculator BOOTS = stack -> {

        if (!(stack.getItem() instanceof ItemArmor item) || item.armorType != 3) {
            return -1.0F;
        }

        if (stack.isItemStackDamageable()) {
            int damageLeft = stack.getMaxDamage() - stack.getItemDamage();
            if (damageLeft < 20) {
                return 0.0F;
            }
        }

        float score = item.damageReduceAmount;

        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 2.5F;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) * 0.1F;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.depthStrider.effectId, stack) * 0.1F;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack) * 0.01F;

        return score;
    };

    ItemScoreCalculator PICKAXE = stack -> {
        if (!(stack.getItem() instanceof ItemPickaxe pickaxe)) {
            return -1.0F;
        }

        float score = pickaxe.getToolMaterial().getEfficiencyOnProperMaterial();

        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency, stack) * 1.25F;

        return score;

    };

    ItemScoreCalculator AXE = stack -> {
        if (!(stack.getItem() instanceof ItemAxe axe)) {
            return -1.0F;
        }

        float score = axe.getToolMaterial().getEfficiencyOnProperMaterial();

        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency, stack) * 1.25F;

        return score;

    };

    ItemScoreCalculator SHOVEL = stack -> {
        if (!(stack.getItem() instanceof ItemSpade shovel)) {
            return -1.0F;
        }

        float score = shovel.getToolMaterial().getEfficiencyOnProperMaterial();

        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency, stack) * 1.25F;

        return score;
    };

    ItemScoreCalculator FISHING_ROD = stack -> {
        if (!(stack.getItem() instanceof ItemFishingRod)) {
            return -1.0F;
        }

        float score = 0.0F;

        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack) * 1.25F;

        return score;
    };

}
