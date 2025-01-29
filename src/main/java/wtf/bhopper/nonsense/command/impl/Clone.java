package wtf.bhopper.nonsense.command.impl;

import net.minecraft.command.CommandBase;
import net.minecraft.item.ItemStack;
import wtf.bhopper.nonsense.command.AbstractCommand;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.util.minecraft.inventory.InventoryUtil;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

@CommandInfo(name = "Clone", description = "Clones your held item (Requires creative mode)", syntax = ".clone [amount]")
public class Clone extends AbstractCommand {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {
        if (!mc.thePlayer.capabilities.isCreativeMode) {
            ChatUtil.error("You must be in creative mode to use that command.");
            return;
        }

        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (heldItem == null) {
            ChatUtil.error("You must be holding an item to use that command.");
            return;
        }

        ItemStack item = heldItem.copy();

        if (args.length >= 2) {
            item.stackSize = CommandBase.parseInt(args[1], 1, 64);
        }

        InventoryUtil.placeStackInInventory(item);

    }
}
