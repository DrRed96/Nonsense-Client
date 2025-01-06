package wtf.bhopper.nonsense.util.minecraft.inventory;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class used to create an ItemStack
 */
public class ItemBuilder {

    private final Item item;
    private int amount = 1;
    private int meta = 0;

    private String displayName = null;
    private String lore = null;

    private int hideFlags = -1;
    private boolean unbreakable = false;

    private final List<Tuple<Enchantment, Integer>> enchantments = new ArrayList<>();
    private final List<Tuple<Enchantment, Integer>> storedEnchantments = new ArrayList<>();
    private final List<PotionEffect> effects = new ArrayList<>();
    private final Map<String, NBTBase> otherTags = new HashMap<>();

    public ItemBuilder(Item item) {
        this.item = item;
    }

    public ItemBuilder(Block block) {
        this.item = Item.getItemFromBlock(block);
    }

    public ItemBuilder setAmount(int amount) {
        if (amount < 1 || amount > 64) {
            throw new IllegalArgumentException("Amount must be between 1 and 64");
        }
        this.amount = amount;
        return this;
    }

    public ItemBuilder setAmountNoLimit(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder setMeta(int meta) {
        this.meta = meta;
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        this.displayName = name;
        return this;
    }

    public ItemBuilder setLore(String lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        this.lore = String.join("\n", lore);
        return this;
    }

    public ItemBuilder setHideFlags(int flags) {
        this.hideFlags = flags;
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment) {
        this.enchantments.add(new Tuple<>(enchantment, enchantment.getMaxLevel()));
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.add(new Tuple<>(enchantment, level));
        return this;
    }

    public ItemBuilder addStoredEnchantment(Enchantment enchantment) {
        this.storedEnchantments.add(new Tuple<>(enchantment, enchantment.getMaxLevel()));
        return this;
    }

    public ItemBuilder addStoredEnchantment(Enchantment enchantment, int level) {
        this.storedEnchantments.add(new Tuple<>(enchantment, level));
        return this;
    }

    public ItemBuilder addPotionEffect(Potion potion, int duration, int amplifier) {
        this.effects.add(new PotionEffect(potion.getId(), duration, amplifier));
        return this;
    }

    public ItemBuilder addPotionEffect(Potion potion, int duration, int amplifier, boolean ambient, boolean showParticles) {
        this.effects.add(new PotionEffect(potion.getId(), duration, amplifier, ambient, showParticles));
        return this;
    }

    public ItemBuilder addPotionEffect(PotionEffect effect) {
        this.effects.add(effect);
        return this;
    }

    public ItemBuilder addTag(String key, NBTBase tag) {
        this.otherTags.put(key, tag);
        return this;
    }

    public ItemStack build() {
        ItemStack stack = new ItemStack(this.item, this.amount, this.meta);

        if (this.displayName != null || this.lore != null) {
            NBTTagCompound display = new NBTTagCompound();
            if (displayName != null) {
                display.setString("Name", this.displayName);
            }
            if (lore != null) {
                NBTTagList loreList = new NBTTagList();
                for (String part : this.lore.split("\n")) {
                    loreList.appendTag(new NBTTagString(part));
                }
                display.setTag("Lore", loreList);
            }
            stack.setTagInfo("display", display);
        }

        for (Tuple<Enchantment, Integer> enchantment : this.enchantments) {
            stack.addEnchantmentNoWrap(enchantment.getFirst(), enchantment.getSecond());
        }

        if (!this.storedEnchantments.isEmpty()) {
            NBTTagList enchNbt = new NBTTagList();
            for (Tuple<Enchantment, Integer> enchantment : this.storedEnchantments) {
                NBTTagCompound enchCompound = new NBTTagCompound();
                enchCompound.setShort("id", (short)enchantment.getFirst().effectId);
                enchCompound.setShort("lvl", enchantment.getSecond().shortValue());
                enchNbt.appendTag(enchCompound);
            }
            stack.setTagInfo("StoredEnchantments", enchNbt);
        }

        if (!this.effects.isEmpty()) {
            NBTTagList potionEffects = new NBTTagList();
            for (PotionEffect effect : this.effects) {
                potionEffects.appendTag(potionCompound(effect));
            }
            stack.setTagInfo("CustomPotionEffects", potionEffects);
        }

        if (this.hideFlags != -1) {
            stack.setTagInfo("HideFlags", new NBTTagInt(this.hideFlags));
        }

        if (this.unbreakable) {
            stack.setTagInfo("Unbreakable", new NBTTagByte((byte)1));
        }

        for (String key : this.otherTags.keySet()) {
            stack.setTagInfo(key, this.otherTags.get(key));
        }

        return stack;

    }

    public static ItemBuilder of(Item item) {
        return new ItemBuilder(item);
    }

    public static ItemBuilder of(Block block) {
        return new ItemBuilder(block);
    }

    public static NBTTagCompound potionCompound(PotionEffect effect) {
        NBTTagCompound compound = new NBTTagCompound();
        effect.writeCustomPotionEffectToNBT(compound);
        return compound;
    }

    public static NBTTagCompound skullCompound(String id, String texture) {
        NBTTagCompound root = new NBTTagCompound();
        root.setString("Id", id);
        NBTTagCompound properties = new NBTTagCompound();
        NBTTagList textures = new NBTTagList();
        NBTTagCompound value = new NBTTagCompound();
        value.setString("Value", texture);
        textures.appendTag(value);
        properties.setTag("textures", textures);
        root.setTag("Properties", properties);
        return root;
    }

}
