package wtf.bhopper.nonsense.util.minecraft.inventory;

import net.minecraft.block.Block;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.List;

public class ItemUtil {

    public static boolean isBlock(ItemStack stack, Block block) {
        if (stack.getItem() instanceof ItemBlock itemBlock) {
            return itemBlock.getBlock() == block;
        }
        return false;
    }

    public static boolean isArmorPiece(ItemStack stack, int armorType) {
        if (stack.getItem() instanceof ItemArmor itemArmor) {
            return itemArmor.armorType == armorType;
        }
        return false;
    }

    public static boolean isPotion(ItemStack stack, Potion potion) {
        if (stack.getItem() instanceof ItemPotion itemPotion) {
            List<PotionEffect> effects = itemPotion.getEffects(stack);
            if (effects == null) {
                return false;
            }

            for (PotionEffect effect : effects) {
                if (effect.getPotionID() == potion.getId()) {
                    return true;
                }
            }

        }

        return false;
    }

    public static int potionAmplifier(ItemStack stack, Potion potion) {
        if (stack.getItem() instanceof ItemPotion itemPotion) {
            List<PotionEffect> effects = itemPotion.getEffects(stack);
            if (effects == null) {
                return -1;
            }

            for (PotionEffect effect : effects) {
                if (effect.getPotionID() == potion.getId()) {
                    return effect.getAmplifier();
                }
            }

        }

        return -1;
    }

    public static int potionDuration(ItemStack stack, Potion potion) {
        if (stack.getItem() instanceof ItemPotion itemPotion) {
            List<PotionEffect> effects = itemPotion.getEffects(stack);
            if (effects == null) {
                return -1;
            }

            for (PotionEffect effect : effects) {
                if (effect.getPotionID() == potion.getId()) {
                    return effect.getDuration();
                }
            }

        }

        return -1;
    }

}
