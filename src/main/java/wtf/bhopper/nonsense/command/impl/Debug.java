package wtf.bhopper.nonsense.command.impl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.module.impl.combat.AntiBot;
import wtf.bhopper.nonsense.module.impl.other.Debugger;
import wtf.bhopper.nonsense.network.Account;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;

import java.io.File;
import java.util.ArrayList;

@CommandInfo(name = "Debug",
        description = "Helps with debugging.",
        syntax = ".debug <args>")
public class Debug extends Command {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {

        if (args.length < 2) {
            ChatUtil.error("Invalid arguments.");
            return;
        }

        switch (args[1].toLowerCase()) {
            case "notification" -> Notification.send("test", "testing 123", NotificationType.INFO, 5000);

            case "hotbar" -> {
                for (int i = 0; i < 9; i++) {
                    ItemStack item = mc.thePlayer.inventory.mainInventory[i];
                    if (item == null) {
                        ChatUtil.debugItem(String.valueOf(i + 1), "Empty");
                        continue;
                    }

                    if (item.getItem() instanceof ItemBlock block) {
                        ChatUtil.debugItems(String.valueOf(i + 1), item.getItem().getUnlocalizedName(), block.getBlock().getUnlocalizedName());
                        continue;
                    }

                    ChatUtil.debugItem(String.valueOf(i + 1), item.getItem().getUnlocalizedName());
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

            case "packet" -> {
                if (args.length < 3) {
                    ChatUtil.error("Invalid arguments");
                    return;
                }

                try {
                    int hash = Integer.parseInt(args[2]);
                    Debugger.PacketInfo packet = Nonsense.module(Debugger.class).cachedPacket(hash);
                    if (packet == null) {
                        ChatUtil.error("That packet was not found");
                        return;
                    }
                    packet.print();

                } catch (NumberFormatException exception) {
                    ChatUtil.error("'%s' is not a number", args[2]);
                }
            }

            case "spb" -> ChatUtil.send("/play build_battle_speed_builders");

            case "ircchat" -> {

                Account account = Account.DEFAULT_ACCOUNT;
                int whisper = 0;

                if (args.length >= 3) {
                    account = switch (args[2].toLowerCase()) {
                        case "admin" -> Account.DEFAULT_ADMIN;
                        case "moderator", "mod" -> Account.DEFAULT_MODERATOR;
                        default -> Account.DEFAULT_ACCOUNT;
                    };

                    if (args.length >= 4) {
                        if (args[3].equalsIgnoreCase("to")) {
                            whisper = 1;
                        } else if (args[3].equalsIgnoreCase("from")) {
                            whisper = 2;
                        }
                    }
                }

                switch (whisper) {
                    case 1 -> ChatUtil.whisper("Hello!", account, true);
                    case 2 -> ChatUtil.whisper("Hello!", account, false);
                    default -> ChatUtil.irc("Hello!", account);
                }

            }

            case "uuids" -> {
                ChatUtil.debugTitle("Player UUID's");
                for (EntityPlayer player : mc.theWorld.getEntities(EntityPlayer.class, player -> !Nonsense.module(AntiBot.class).isBot(player))) {
                    ChatUtil.debugItem(player.getName(), player.getUniqueID());
                }
            }

            case "explodecrash" -> PacketUtil.receive(new S27PacketExplosion(mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    1.0F,
                    new ArrayList<>(),
                    new Vec3(Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE)));

            default -> ChatUtil.error("Unknown debugging command.");

        }

    }
}
