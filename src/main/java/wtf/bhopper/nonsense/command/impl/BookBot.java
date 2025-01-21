package wtf.bhopper.nonsense.command.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.util.minecraft.inventory.ItemBuilder;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(name = "BookBot", description = "Fills a book and quill with jumbled nonsense.", syntax = ".bookbot")
public class BookBot extends Command {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (heldItem == null || heldItem.getItem() != Items.writable_book) {
            ChatUtil.error("You must be holding a Book and Quill to use Book Bot.");
            return;
        }

        List<JsonObject> pagesJson = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            JsonObject object = new JsonObject();
            object.addProperty("text", GeneralUtil.randomString(256));
            pagesJson.add(object);
        }

        List<String> pages = pagesJson.stream()
                .map(JsonElement::toString)
                .toList();

        NBTTagList pagesNbt = new NBTTagList();
        for (String page : pages) {
            pagesNbt.appendTag(new NBTTagString(page));
        }

        ItemStack book = ItemBuilder.of(Items.written_book)
                .addTag("pages", pagesNbt)
                .addTag("author", new NBTTagString(mc.thePlayer.getName()))
                .addTag("title", new NBTTagString("Book Bot"))
                .build();

        PacketBuffer buffer = PacketUtil.createBuffer();
        buffer.writeItemStackToBuffer(book);
        PacketUtil.send(new C17PacketCustomPayload("MC|BSign", buffer));

    }
}
