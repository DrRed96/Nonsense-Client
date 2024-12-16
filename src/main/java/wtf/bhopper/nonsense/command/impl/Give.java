package wtf.bhopper.nonsense.command.impl;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.InventoryUtil;

@CommandInfo(name = "Give",
        description = "Gives you any item (Requires creative mode)",
        syntax = ".give <item> [amount] [meta] [nbt]")
public class Give extends Command {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {

        // Implementation of the /give command

        if (!mc.thePlayer.capabilities.isCreativeMode) {
            ChatUtil.error("You must be in creative mode to use that command.");
            return;
        }

        if (args.length < 2) {
            ChatUtil.error("Invalid arguments: %s", syntax);
            return;
        }

        Item item = CommandBase.getItemByText(mc.thePlayer, args[1]);
        int amount = args.length >= 3 ? CommandBase.parseInt(args[2], 1, 64) : 1;
        int meta = args.length >= 4 ? CommandBase.parseInt(args[3]) : 0;

        ItemStack stack = new ItemStack(item, amount, meta);

        if (args.length >= 5) {
            String nbt = CommandBase.getChatComponentFromNthArg(mc.thePlayer, args, 4).getUnformattedText();

            try {
                stack.setTagCompound(JsonToNBT.getTagFromJson(nbt));
            } catch (NBTException nbtexception) {
                throw new CommandException("commands.give.tagError", nbtexception.getMessage());
            }
        }


        if (InventoryUtil.placeStackInInventory(stack)) {
            ChatUtil.info("Added item to inventory: %s", stack);
        } else {
            ChatUtil.error("No slot available");
        }

    }
}
