package wtf.bhopper.nonsense.command.impl;

import net.minecraft.item.ItemStack;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;

@CommandInfo(name = "ItemInfo", description = "Displays the information of an item", syntax = ".iteminfo")
public class ItemInfo extends Command {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {
        if (mc.thePlayer.getHeldItem() == null) {
            ChatUtil.error("No held item");
            return;
        }

        ItemStack stack = mc.thePlayer.getHeldItem();

        ChatUtil.debugTitle("Item Info");
        ChatUtil.debugItem("Item", stack.getItem().getUnlocalizedName());
        ChatUtil.debugItem("Amount", stack.stackSize);
        ChatUtil.debugItem("Metadata", String.format("%d (0x%X)", stack.getMetadata(), stack.getMetadata()));
        if (stack.hasDisplayName()) {
            ChatUtil.debugItem("Display Name", stack.getDisplayName());
        }
        if (stack.hasTagCompound()) {
            ChatUtil.debugItem("NBT", stack.getTagCompound().toString());
        }
    }
}
