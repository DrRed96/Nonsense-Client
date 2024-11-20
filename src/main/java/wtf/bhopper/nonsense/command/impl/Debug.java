package wtf.bhopper.nonsense.command.impl;

import net.minecraft.event.ClickEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.EnumChatFormatting;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;

import java.io.File;
import java.io.IOException;

@CommandInfo(name = "Debug", description = "Helps with debugging", syntax = ".debug <args>")
public class Debug extends Command {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {

        if (args.length < 2) {
            ChatUtil.error("Invalid arguments.");
            return;
        }

        switch (args[1].toLowerCase()) {
            case "notification" -> Notification.send("test", "testing 123", NotificationType.INFO, 5000);

            case "iteminfo" -> {
                if (mc.thePlayer.getHeldItem() == null) {
                    ChatUtil.error("No held item");
                    return;
                }

                ItemStack stack = mc.thePlayer.getHeldItem();

                ChatUtil.debugTitle("Item Info");
                ChatUtil.debugItem("Item", stack.getItem().getUnlocalizedName());
                ChatUtil.debugItem("Amount", stack.stackSize);
                ChatUtil.debugItem("Metadata", String.format("%d | 0x%X", stack.getMetadata(), stack.getMetadata()));
                if (stack.hasDisplayName()) {
                    ChatUtil.debugItem("Display Name", stack.getDisplayName());
                }
                if (stack.hasTagCompound()) {
                    ChatUtil.debugItem("NBT", stack.getTagCompound().toString());
                }
            }

            case "nbtdump" -> {
                if (mc.thePlayer.getHeldItem() == null) {
                    ChatUtil.error("No held item");
                    return;
                }

                ItemStack stack = mc.thePlayer.getHeldItem();

                if (!stack.hasTagCompound()) {
                    ChatUtil.error("Item does not have any NBT data");
                    return;
                }

                File dir = Nonsense.getDataDir().toPath().resolve("nbtdump").toFile();
                dir.mkdirs();


                File file = dir.toPath().resolve(String.format("dump_%08x.nbt", stack.hashCode())).toFile();



                CompressedStreamTools.write(stack.getTagCompound(), file);

                ChatUtil.Builder.of("%sDumped NBT data to file: %s", ChatUtil.DEBUG_PREFIX, file.getPath())
                        .setColor(EnumChatFormatting.AQUA)
                        .setHoverEvent("Click to open file")
                        .setClickEvent(ClickEvent.Action.OPEN_FILE, file.getPath())
                        .send();


            }

        }

    }
}
