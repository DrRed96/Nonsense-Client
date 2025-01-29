package wtf.bhopper.nonsense.command.impl;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import wtf.bhopper.nonsense.command.AbstractCommand;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

@CommandInfo(name = "ItemInfo",
        description = "Displays the information of an item",
        syntax = ".iteminfo")
public class ItemInfo extends AbstractCommand {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {

        ItemStack stack = mc.thePlayer.getHeldItem();

        if (stack == null) {
            ChatUtil.error("No held item");
            return;
        }

        ChatUtil.debugTitle("Item Info");
        ChatUtil.debugItem("Item", String.format("%s (#%04d)", stack.getItem().getUnlocalizedName(), Item.getIdFromItem(stack.getItem())));
        ChatUtil.debugItem("Amount", stack.stackSize);
        ChatUtil.debugItem("Metadata", String.format("%d / 0x%X", stack.getMetadata(), stack.getMetadata()));
        if (stack.hasDisplayName()) {
            ChatUtil.debugItem("Display Name", stack.getDisplayName());
        }
        if (stack.hasTagCompound()) {
            ChatUtil.debugItem("NBT", stack.getTagCompound().toString());
        }
    }
}
