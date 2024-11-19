package wtf.bhopper.nonsense.gui.screens.creative;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.ItemBuilder;

import java.util.List;

@SuppressWarnings("unsued")
public class CustomCreativeTabs extends CreativeTabs {
    public static final CreativeTabs[] TABS = new CreativeTabs[6];

    public static final CustomCreativeTabs tabUnobtainable = new CustomCreativeTabs(0, "Exploits", Item.getItemFromBlock(Blocks.barrier), items -> {
        items.add(new ItemStack(Blocks.command_block));
        items.add(new ItemStack(Blocks.mob_spawner));
        items.add(new ItemStack(Blocks.barrier));
        items.add(new ItemStack(Items.command_block_minecart));

        items.add(new ItemStack(Items.spawn_egg, 1, 63));
        items.add(new ItemStack(Items.spawn_egg, 1, 64));
        items.add(new ItemStack(Items.spawn_egg, 1, 53));
        items.add(new ItemStack(Items.spawn_egg, 1, 99));
        items.add(new ItemStack(Items.spawn_egg, 1, 97));
        items.add(new ItemStack(Items.spawn_egg, 1, 200));

        items.add(ItemBuilder.of(Items.potionitem)
                .setMeta(0x4001) // 0x4000 for the splash potion and 0x1 for the color
                .addPotionEffect(Potion.blindness, 1000000, 2)
                .addPotionEffect(Potion.moveSlowdown, 1000000, 2)
                .addPotionEffect(Potion.confusion, 1000000, 2)
                .addPotionEffect(Potion.poison, 1000000, 2)
                .addPotionEffect(Potion.wither, 1000000, 2)
                .addPotionEffect(Potion.weakness, 1000000, 2)
                .addPotionEffect(Potion.hunger, 1000000, 2)
                .addPotionEffect(Potion.digSlowdown, 1000000, 2)
                .build());

        items.add(ItemBuilder.of(Items.potionitem)
                .setMeta(0x4001)
                .addPotionEffect(Potion.invisibility, 1000000, 2)
                .build());

        items.add(ItemBuilder.of(Items.potionitem)
                .setMeta(0x4001)
                .addPotionEffect(Potion.heal, 0, 125)
                .build());

        items.add(ItemBuilder.of(Blocks.anvil)
                .setMeta(420)
                .setDisplayName("\247c\247lForce OP v2")
                .build());

        items.add(ItemBuilder.of(Blocks.anvil)
                .setMeta(Short.MAX_VALUE)
                .setDisplayName("\247rnull")
                .build());

    });


    public static final CustomCreativeTabs tabTest = new CustomCreativeTabs(1, "OP Items", Items.diamond_sword, items -> {

        items.add(ItemBuilder.of(Items.diamond_sword)
                .addEnchantment(Enchantment.sharpness, Short.MAX_VALUE)
                .addEnchantment(Enchantment.unbreaking, Short.MAX_VALUE)
                .addEnchantment(Enchantment.knockback, 10)
                .addEnchantment(Enchantment.fireAspect, Short.MAX_VALUE)
                .addEnchantment(Enchantment.looting, 10)
                .build());

        items.add(ItemBuilder.of(Items.diamond_pickaxe)
                .addEnchantment(Enchantment.efficiency, Short.MAX_VALUE)
                .addEnchantment(Enchantment.fortune, 10)
                .addEnchantment(Enchantment.unbreaking, Short.MAX_VALUE)
                .build());

        items.add(ItemBuilder.of(Items.diamond_pickaxe)
                .addEnchantment(Enchantment.efficiency, Short.MAX_VALUE)
                .addEnchantment(Enchantment.silkTouch, 10)
                .addEnchantment(Enchantment.unbreaking, Short.MAX_VALUE)
                .build());

        items.add(ItemBuilder.of(Items.diamond_axe)
                .addEnchantment(Enchantment.efficiency, Short.MAX_VALUE)
                .addEnchantment(Enchantment.silkTouch, 10)
                .addEnchantment(Enchantment.unbreaking, Short.MAX_VALUE)
                .build());

        items.add(ItemBuilder.of(Items.diamond_shovel)
                .addEnchantment(Enchantment.efficiency, Short.MAX_VALUE)
                .addEnchantment(Enchantment.silkTouch, 10)
                .addEnchantment(Enchantment.unbreaking, Short.MAX_VALUE)
                .build());

        items.add(ItemBuilder.of(Items.diamond_hoe)
                .addEnchantment(Enchantment.efficiency, Short.MAX_VALUE)
                .addEnchantment(Enchantment.silkTouch, 10)
                .addEnchantment(Enchantment.unbreaking, Short.MAX_VALUE)
                .build());

        items.add(ItemBuilder.of(Items.diamond_helmet)
                .addEnchantment(Enchantment.protection, Short.MAX_VALUE)
                .addEnchantment(Enchantment.thorns, Short.MAX_VALUE)
                .addEnchantment(Enchantment.unbreaking, Short.MAX_VALUE)
                .addEnchantment(Enchantment.respiration, Short.MAX_VALUE)
                .addEnchantment(Enchantment.aquaAffinity, 10)
                .build());

        items.add(ItemBuilder.of(Items.diamond_chestplate)
                .addEnchantment(Enchantment.protection, Short.MAX_VALUE)
                .addEnchantment(Enchantment.thorns, Short.MAX_VALUE)
                .addEnchantment(Enchantment.unbreaking, Short.MAX_VALUE)
                .build());

        items.add(ItemBuilder.of(Items.diamond_leggings)
                .addEnchantment(Enchantment.protection, Short.MAX_VALUE)
                .addEnchantment(Enchantment.thorns, Short.MAX_VALUE)
                .addEnchantment(Enchantment.unbreaking, Short.MAX_VALUE)
                .build());

        items.add(ItemBuilder.of(Items.diamond_boots)
                .addEnchantment(Enchantment.protection, Short.MAX_VALUE)
                .addEnchantment(Enchantment.thorns, Short.MAX_VALUE)
                .addEnchantment(Enchantment.unbreaking, Short.MAX_VALUE)
                .addEnchantment(Enchantment.featherFalling, Short.MAX_VALUE)
                .addEnchantment(Enchantment.depthStrider, 10)
                .build());

        items.add(ItemBuilder.of(Items.bow)
                .addEnchantment(Enchantment.power, Short.MAX_VALUE)
                .addEnchantment(Enchantment.unbreaking, Short.MAX_VALUE)
                .addEnchantment(Enchantment.punch, 10)
                .addEnchantment(Enchantment.flame, 10)
                .addEnchantment(Enchantment.infinity, 10)
                .build());

        items.add(ItemBuilder.of(Items.fishing_rod)
                .addEnchantment(Enchantment.unbreaking, Short.MAX_VALUE)
                .addEnchantment(Enchantment.lure, 10)
                .addEnchantment(Enchantment.luckOfTheSea, 10)
                .addEnchantment(Enchantment.knockback, 10)
                .build());

        items.add(ItemBuilder.of(Items.flint_and_steel)
                .addEnchantment(Enchantment.unbreaking, Short.MAX_VALUE)
                .build());

        items.add(ItemBuilder.of(Items.diamond_sword)
                .addEnchantment(Enchantment.sharpness)
                .addEnchantment(Enchantment.unbreaking)
                .addEnchantment(Enchantment.knockback)
                .addEnchantment(Enchantment.fireAspect)
                .addEnchantment(Enchantment.looting)
                .build());

        items.add(ItemBuilder.of(Items.diamond_sword)
                .addEnchantment(Enchantment.sharpness, 10)
                .addEnchantment(Enchantment.unbreaking, 10)
                .addEnchantment(Enchantment.knockback, 10)
                .addEnchantment(Enchantment.fireAspect, 10)
                .addEnchantment(Enchantment.looting, 10)
                .build());

    });

    public static final CustomCreativeTabs tabExploits = new CustomCreativeTabs(2, "Banners", Items.banner, items -> {
        for (int i = 0; i < 16; i++) {
            items.add(new ItemStack(Items.banner, 1, i));
        }
    });

    public static final CustomCreativeTabs tabHypixel = new CustomCreativeTabs(3, "Hypixel", Items.gold_ingot, items -> {

        NBTTagCompound menu1Attributes = new NBTTagCompound();
        menu1Attributes.setString("HOUSING_MENU", "OWNER");
        items.add(ItemBuilder.of(Items.nether_star)
                .setDisplayName("\247dHousing Menu\2477 (Right Click)")
                .setHideFlags(254)
                .addTag("ExtraAttributes", menu1Attributes)
                .build());

        NBTTagCompound menu2Attributes = new NBTTagCompound();
        menu2Attributes.setString("HOUSING_MENU", "TRUSTED_BUILDER");
        items.add(ItemBuilder.of(Items.ghast_tear)
                .setDisplayName("\247aHousing Menu\2477 (Right Click)")
                .setHideFlags(254)
                .addTag("ExtraAttributes", menu2Attributes)
                .build());

        NBTTagCompound menu3Attributes = new NBTTagCompound();
        menu3Attributes.setString("HOUSING_MENU", "GUEST");
        items.add(ItemBuilder.of(Items.dark_oak_door)
                .setDisplayName("\247aHousing Menu\2477 (Right Click)")
                .setHideFlags(254)
                .addTag("ExtraAttributes", menu3Attributes)
                .build());

        items.add(ItemBuilder.of(Blocks.end_portal_frame)
                .setDisplayName("\247aTeleport Pad")
                .setLore("\2477Place this block in your house to", "\2477place a Teleport Pad!")
                .build());

        items.add(ItemBuilder.of(Blocks.slime_block)
                .setDisplayName("\247aLaunch Pad")
                .setLore(
                        "\2477Place this block in your house to",
                        "\2477place a Launch Pad!"
                )
                .addTag("overrideMeta", new NBTTagByte((byte) 1))
                .addTag("AttributeModifiers", new NBTTagList())
                .build());

        items.add(ItemBuilder.of(Blocks.light_weighted_pressure_plate)
                .setDisplayName("\247aParkour Block")
                .setLore()
                .build());

        items.add(ItemBuilder.of(Blocks.heavy_weighted_pressure_plate)
                .setDisplayName("\247aAction Pad")
                .setLore(
                        "\2477Place this block in your house to",
                        "\2477place an Action Pad!"
                )
                .build());

        items.add(ItemBuilder.of(Items.name_tag)
                .setDisplayName("\247aHologram")
                .setLore(
                        "\2477Place this in your house to place a",
                        "\2477Hologram!"
                )
                .build());

        items.add(ItemBuilder.of(Items.skull)
                .setMeta(3)
                .setDisplayName("\247aNPC")
                .setLore(
                        "\2477Place this in your house to place an",
                        "\2477NPC!"
                )
                .build());

        items.add(ItemBuilder.of(Blocks.stone_button)
                .setDisplayName("\247aAction Button")
                .setLore(
                        "\2477Place this block in your house to",
                        "\2477place an Action Button!"
                )
                .build());

        items.add(ItemBuilder.of(Items.book)
                .setDisplayName("\247aStat Leaderboard")
                .setLore(
                        "\2477Place this in your house to place a",
                        "\2477Stat Leaderboard!"
                )
                .build());

        items.add(ItemBuilder.of(Items.book)
                .setDisplayName("\247aParkour Leaderboard")
                .setLore(
                        "\2477Place this in your house to place a",
                        "\2477parkour leaderboard!"
                )
                .build());

        NBTTagCompound trashCanAttributes = new NBTTagCompound();
        trashCanAttributes.setString("ITEM_ID", "TRASH_CAN");
        items.add(ItemBuilder.of(Items.cauldron)
                .setDisplayName("\247aTrash Can")
                .setLore(
                        "\2477Place this block in your house to",
                        "\2477place a Trash Cann!"
                )
                .addTag("ExtraAttributes", trashCanAttributes)
                .build());

        NBTTagCompound biomeStickAttributes = new NBTTagCompound();
        NBTTagCompound biomeStickData = new NBTTagCompound();
        biomeStickData.setTag("biomeType", new NBTTagString("DESERT"));
        biomeStickData.setTag("radius", new NBTTagInt(3));
        biomeStickAttributes.setTag("data", biomeStickData);
        biomeStickAttributes.setString("ITEM_ID", "BIOME_STICK");
        items.add(ItemBuilder.of(Items.stick)
                .setDisplayName("\247aBiome Stick\2477 - \247eDesert\2477 (3)")
                .setLore(
                        "\2477Allows you to change the Biome at",
                        "\2477the \247atargeted block\2477!",
                        "",
                        "\2477Biome: \247eDesert",
                        "\2477Radius: \247e3",
                        "",
                        "\247eLeft Click to change settings!",
                        "\247eRight Click to use!"
                )
                .addTag("ench", new NBTTagList())
                .addTag("ExtraAttributes", biomeStickAttributes)
                .build());

        items.add(ItemBuilder.of(Items.nether_star)
                .setDisplayName("\247bOptions \2477(Right Click)")
                .setLore(
                        "\2477Right click to access the menu for",
                        "\2477your plot!"
                )
                .build());

        // skyblock lol
        items.add(ItemBuilder.of(Items.diamond_sword)
                .setDisplayName("\2476Aspect of the Dragons")
                .setLore(
                        "\2477Damage: \247c+225",
                        "\2477Strength: \247c+100",
                        " \2478[\u2741]",
                        "",
                        "\2476Ability: Dragon Rage \247e\247lRIGHT CLICK",
                        "\2477All Monsters in front of you take",
                        "\2477\247a12,000\2477 damage. Hit monsters take",
                        "\2477large knockback.",
                        "\2478Mana Cost: \2473100",
                        "",
                        "\2478This item can be reforged!",
                        "\2476\247lLEGENDARY SWORD"
                )
                .setHideFlags(63)
                .build());

    });

    public static final CustomCreativeTabs tabStupid = new CustomCreativeTabs(4, "Nonsense", Item.getItemFromBlock(Blocks.deadbush), items -> {

        items.add(ItemBuilder.of(Items.stick)
                .setDisplayName("\247c\247lS\2476\247lT\247e\247lI\247a\247lC\247b\247lK \2479\247lO\247d\247lF \247c\247lD\2476\247lE\247e\247lS\247a\247lT\247b\247lI\2479\247lN\247d\247lY")
                .setLore(
                        "",
                        "\247eOi you! Yes you. What are you",
                        "\247elooking at? Yes this stick has level",
                        "\247e32k enchantments. Kinda overkill?",
                        "\247e1'm lazy ok. \247c(\u256F\u00B0\u25A1\u00B0)\u256F\2477\uFE35 \u253B\u2501\u253B"
                )
                .addEnchantment(Enchantment.sharpness, Short.MAX_VALUE)
                .addEnchantment(Enchantment.unbreaking, Short.MAX_VALUE)
                .addEnchantment(Enchantment.knockback, 10)
                .addEnchantment(Enchantment.fireAspect, Short.MAX_VALUE)
                .addEnchantment(Enchantment.looting, 10)
                .build());

        items.add(ItemBuilder.of(Items.wooden_sword)
                .setDisplayName("\2478THE WORST WEAPON EVER")
                .setLore(
                        "\2477Bro... this sword is so SHIT!!!",
                        "\2477srsly who tf would use this thing???"
                )
                .addEnchantment(Enchantment.sharpness, Short.MIN_VALUE)
                .addEnchantment(Enchantment.unbreaking, Short.MIN_VALUE)
                .addEnchantment(Enchantment.knockback, Short.MIN_VALUE)
                .addEnchantment(Enchantment.fireAspect, Short.MIN_VALUE)
                .addEnchantment(Enchantment.looting, Short.MIN_VALUE)
                .build());

        ItemBuilder bigBook = ItemBuilder.of(Items.enchanted_book).setDisplayName("BIG BOOK");
        for (Enchantment enchantment : Enchantment.enchantmentsList) {
            if (enchantment != null) {
                bigBook.addStoredEnchantment(enchantment, enchantment.getMaxLevel());
            }
        }
        items.add(bigBook.build());

        ItemBuilder biggerBook = ItemBuilder.of(Items.enchanted_book).setDisplayName("BIGGER BOOK");
        ;
        for (Enchantment enchantment : Enchantment.enchantmentsList) {
            if (enchantment != null) {
                biggerBook.addStoredEnchantment(enchantment, 10);
            }
        }
        items.add(biggerBook.build());

        ItemBuilder biggestBook = ItemBuilder.of(Items.enchanted_book).setDisplayName("BIGGEST BOOK");
        ;
        for (Enchantment enchantment : Enchantment.enchantmentsList) {
            if (enchantment != null) {
                biggestBook.addStoredEnchantment(enchantment, Short.MAX_VALUE);
            }
        }
        items.add(biggestBook.build());

        items.add(ItemBuilder.of(Items.fish)
                .setDisplayName("\247baswdfzxcvbhgtyyn")
                .setLore("F1Sh R Fr3Nds", "N0t Fudee!1!!")
                .setHideFlags(1)
                .addEnchantment(Enchantment.infinity)
                .addEnchantment(Enchantment.knockback)
                .build());

        items.add(ItemBuilder.of(Blocks.deadbush)
                .setHideFlags(1)

                .addEnchantment(Enchantment.infinity)
                .build());

        items.add(ItemBuilder.of(Items.skull)
                .setMeta(3)
                .setDisplayName("\2476Arithmo's Head")
                .addTag("SkullOwner", ItemBuilder.skullCompound("fe7dbb41-998c-554f-812f-058604a39955", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmNiNWMwMmQzMDNiNGZkMjViM2JhYzVmY2M1YmI5YTI0ZGZlMWQ2ZjM0Yzg5YzY3Y2IwYTViYmY0NThjMWE4In19fQ=="))
                .build());

        items.add(ItemBuilder.of(Items.skull)
                .setMeta(3)
                .setDisplayName("\2476aswdfzxcvbhgtyyn's Head")
                .addTag("SkullOwner", ItemBuilder.skullCompound("51818755-c60f-5d6f-a771-b9889d1c75d7", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzM1N2MyNWI1NzQ4Mjg1Njg4M2Q2NWI1MmI0OGIxYzNiNjI3MWQxMmVhMjZjOWI0NGM5NDI4MTJlNTcyIn19fQ=="))
                .build());

        items.add(ItemBuilder.of(Items.skull)
                .setMeta(3)
                .setDisplayName("\2476CalculusHvH's Head")
                .addTag("SkullOwner", ItemBuilder.skullCompound("30880b64-84a0-5a1e-aceb-9c5660984230", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGM4ZThlMjY2YmU4ZGY1NTk5ZjZhMDBlMGRkNDg2NmFiMWFiZTdjYzc1OTYyZWYzMjA0YjI1ZjlkNzJiNmExYSJ9fX0="))
                .build());

        items.add(ItemBuilder.of(Items.skull)
                .setMeta(3)
                .setDisplayName("\2476DalekLogic's Head")
                .addTag("SkullOwner", ItemBuilder.skullCompound("4f6e6c27-465a-54b9-a2e0-c9d51006d877", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE5MWIyODI1NWViY2RiMDFkYzI0YWFmODU0YzllMjA1YTk1YWU0MWY3YzdjZTQ1ZDk2ZjQyNWM3MmQ4MTEyNiJ9fX0="))
                .build());

        items.add(ItemBuilder.of(Items.skull)
                .setMeta(3)
                .setDisplayName("\2476REZA's Head")
                .addTag("SkullOwner", ItemBuilder.skullCompound("dae5a4e4-bf48-58fe-a42f-27ecdacf9b2b", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmVmZWFmNGUxOThjOWViZDNmN2ZiNmMxNTYxZGJkNjBkMDg5YzJmZWNkZGQ4ZTdkMjM2ZGFkY2U2ZmZiM2UzYiJ9fX0="))
                .build());

        items.add(ItemBuilder.of(Items.skull)
                .setMeta(3)
                .setDisplayName("\2476Technoblade's Head")
                .addTag("SkullOwner", ItemBuilder.skullCompound("3e256793-f343-5f21-8f79-d48e202968a2", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNhNTM4Zjc4NzA0OGRiYTI3ZGNkYmJjYjcyZDJmNTc4Zjg1NzczMTY4ZDcyNDY2MjY2ZTc1NWY0NzFjODkifX19"))
                .build());

        items.add(ItemBuilder.of(Items.skull)
                .setMeta(3)
                .setDisplayName("\2476Xylan's Head")
                .addTag("SkullOwner", ItemBuilder.skullCompound("966d29d4-d98a-5a8e-a7a1-a376bd203d40", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTI5NjAxNmRjZTkyOWQ5MDJkNWUwMDA0Mjk4M2MxZDA1ZmVmZjA1YWM5MDgxMzQyMzMxODY2MGQ0NjRjMDE2In19fQ=="))
                .build());

        items.add(ItemBuilder.of(Items.skull)
                .setMeta(3)
                .setDisplayName("\2476zarzel's Head")
                .addTag("SkullOwner", ItemBuilder.skullCompound("8ef75223-d9d7-55db-9598-a1056549b018", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDAwNzE3MjM4MzVjZDBjYTljNmU5ZTFiNTc1ZjZiODg2ZTIzYjJjNDA4OWVhNDQxMjhlYmU3YzIxMTY1N2MyZiJ9fX0="))
                .build());

        items.add(ItemBuilder.of(Items.cake)
                .setAmount(64)
                .build());

        items.add(ItemBuilder.of(Items.diamond_sword)
                .setAmount(64)
                .build());

        items.add(ItemBuilder.of(Items.diamond)
                .setAmountNoLimit(-1)
                .build());

        NBTTagList godPage = new NBTTagList();
        godPage.appendTag(new NBTTagString("I hope you get cancer."));
        items.add(ItemBuilder.of(Items.written_book)
                .addTag("pages", godPage)
                .addTag("author", new NBTTagString("\247cGOD"))
                .addTag("title", new NBTTagString("\2476A MESSAGE FROM GOD"))
                .addTag("generation", new NBTTagInt(3)) // Makes the book 'Tattered' (meaning it can't be copied)
                .build());


        NBTTagList opPages = new NBTTagList();
        opPages.appendTag(new NBTTagString("{\"extra\":[{\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/gamemode 1 @p\"},\"text\":\"Creative\"},{\"color\":\"reset\",\"text\":\"\\n-------------------\\n\"},{\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/gamemode 0 @p\"},\"text\":\"Survival\"},{\"color\":\"reset\",\"text\":\"\\n-------------------\\n\"},{\"bold\":true,\"underlined\":true,\"color\":\"dark_blue\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/give @p bedrock 64\"},\"text\":\"Give 64 Bedrock\"}],\"text\":\"\"}"));

        items.add(ItemBuilder.of(Items.written_book)
                .addTag("pages", opPages)
                .addTag("author", new NBTTagString("\uFF30\uFF59\uFF52\uFF4F\uFF42\uFF59\uFF54\uFF45"))
                .addTag("title", new NBTTagString("\uFF22\uFF41\uFF43\uFF4B\uFF44\uFF4F\uFF4F\uFF52 \uFF22\uFF4F\uFF4F\uFF4B"))
                .addTag("generation", new NBTTagInt(3))
                .build());
    });

    public static final CreativeTabs tabInventory = (new CreativeTabs(5, "Survival Inventory", TABS) {
        public Item getTabIconItem() {
            return Item.getItemFromBlock(Blocks.chest);
        }
    }).setBackgroundImageName("inventory.png").setNoScrollbar().setNoTitle();

    private final Item icon;

    public final ItemAdder itemAdder;

    private final int columnOverride;

    public CustomCreativeTabs(int index, String label, Item icon, ItemAdder itemAdder) {
        this(index, label, icon, index, itemAdder);
    }

    public CustomCreativeTabs(int index, String label, Item icon, int columnOverride, ItemAdder itemAdder) {
        super(index, label, TABS);
        this.icon = icon;
        this.itemAdder = itemAdder;
        this.columnOverride = columnOverride;
    }

    @Override
    public Item getTabIconItem() {
        return icon;
    }

    @Override
    public void displayAllReleventItems(List<ItemStack> itemStacks) {
        this.itemAdder.addItems(itemStacks);
    }

    @Override
    public int getTabColumn() {
        return columnOverride % 6;
    }

    public interface ItemAdder {
        void addItems(List<ItemStack> items);
    }
}
