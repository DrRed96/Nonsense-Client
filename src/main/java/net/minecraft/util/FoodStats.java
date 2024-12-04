package net.minecraft.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;

public class FoodStats
{
    /** The player's food level. */
    private int foodLevel = 20;

    /** The player's food saturation. */
    private float foodSaturationLevel = 5.0F;

    /** The player's food exhaustion. */
    private float foodExhaustionLevel;

    /** The player's food timer value. */
    private int foodTimer;
    private int prevFoodLevel = 20;

    /**
     * Add food stats.
     */
    public void addStats(int foodLevelIn, float foodSaturationModifier)
    {
        this.foodLevel = Math.min(foodLevelIn + this.foodLevel, 20);
        this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float)foodLevelIn * foodSaturationModifier * 2.0F, (float)this.foodLevel);
    }

    public void addStats(ItemFood foodItem, ItemStack p_151686_2_)
    {
        this.addStats(foodItem.getHealAmount(p_151686_2_), foodItem.getSaturationModifier(p_151686_2_));
    }

    /**
     * Handles the food game logic.
     */
    public void onUpdate(EntityPlayer player)
    {
        EnumDifficulty enumdifficulty = player.worldObj.getDifficulty();
        this.prevFoodLevel = this.foodLevel;

        if (this.foodExhaustionLevel > 4.0F)
        {
            this.foodExhaustionLevel -= 4.0F;

            if (this.foodSaturationLevel > 0.0F)
            {
                this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
            }
            else if (enumdifficulty != EnumDifficulty.PEACEFUL)
            {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        if (player.worldObj.getGameRules().getBoolean("naturalRegeneration") && this.foodLevel >= 18 && player.shouldHeal())
        {
            ++this.foodTimer;

            if (this.foodTimer >= 80)
            {
                player.heal(1.0F);
                this.addExhaustion(3.0F);
                this.foodTimer = 0;
            }
        }
        else if (this.foodLevel <= 0)
        {
            ++this.foodTimer;

            if (this.foodTimer >= 80)
            {
                if (player.getHealth() > 10.0F || enumdifficulty == EnumDifficulty.HARD || player.getHealth() > 1.0F && enumdifficulty == EnumDifficulty.NORMAL)
                {
                    player.attackEntityFrom(DamageSource.starve, 1.0F);
                }

                this.foodTimer = 0;
            }
        }
        else
        {
            this.foodTimer = 0;
        }
    }

    /**
     * Reads the food data for the player.
     */
    public void readNBT(NBTTagCompound nbt)
    {
        if (nbt.hasKey("foodLevel", 99))
        {
            this.foodLevel = nbt.getInteger("foodLevel");
            this.foodTimer = nbt.getInteger("foodTickTimer");
            this.foodSaturationLevel = nbt.getFloat("foodSaturationLevel");
            this.foodExhaustionLevel = nbt.getFloat("foodExhaustionLevel");
        }
    }

    /**
     * Writes the food data for the player.
     */
    public void writeNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("foodLevel", this.foodLevel);
        nbt.setInteger("foodTickTimer", this.foodTimer);
        nbt.setFloat("foodSaturationLevel", this.foodSaturationLevel);
        nbt.setFloat("foodExhaustionLevel", this.foodExhaustionLevel);
    }

    /**
     * Get the player's food level.
     */
    public int getFoodLevel()
    {
        return this.foodLevel;
    }

    public int getPrevFoodLevel()
    {
        return this.prevFoodLevel;
    }

    /**
     * Get whether the player must eat food.
     */
    public boolean needFood()
    {
        return this.foodLevel < 20;
    }

    /**
     * adds input to foodExhaustionLevel to a max of 40
     */
    public void addExhaustion(float amount)
    {
        this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + amount, 40.0F);
    }

    /**
     * Get the player's food saturation level.
     */
    public float getSaturationLevel()
    {
        return this.foodSaturationLevel;
    }

    public void setFoodLevel(int foodLevelIn)
    {
        this.foodLevel = foodLevelIn;
    }

    public void setFoodSaturationLevel(float foodSaturationLevelIn)
    {
        this.foodSaturationLevel = foodSaturationLevelIn;
    }
}
