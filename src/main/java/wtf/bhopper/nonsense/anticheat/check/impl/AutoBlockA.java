package wtf.bhopper.nonsense.anticheat.check.impl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import wtf.bhopper.nonsense.anticheat.check.AbstractCheck;
import wtf.bhopper.nonsense.anticheat.check.CheckInfo;
import wtf.bhopper.nonsense.anticheat.PlayerData;
import wtf.bhopper.nonsense.anticheat.check.data.AbstractCheckBuffer;

@CheckInfo(
        name = "Auto-Block A",
        description = "Unlikely sword blocking/unblocking.",
        maxVl = 3
)
public class AutoBlockA extends AbstractCheck {

    @Override
    public void handleEntityMetadata(PlayerData data, S1CPacketEntityMetadata packet) {
        if (packet.getMetadata() == null) {
            return;
        }

        int maxBlocks = this.getMaxBlocks(data.getEntity());
        Buffer buffer = data.getCheckData(Buffer.class, Buffer::new);

        if (maxBlocks != -1 && data.getBlocks() > maxBlocks) {
            if (buffer.incrementBuffer() > 2.0F) {
                data.incrementVl(this);
            }
        } else {
            buffer.decrementBuffer(0.1F);
        }

    }

    @Override
    public void handleRelMove(PlayerData data, S14PacketEntity packet) {
        if (!data.isHacking()) {
            data.decrementVl(this, 0.985F);
        }
    }

    private int getMaxBlocks(EntityPlayer player) {
        ItemStack itemStack = player.getHeldItem();
        if (itemStack == null) {
            return -1;
        }

        Item item = itemStack.getItem();

        if (item == Items.bow) {
            return 4;
        }

        if (item instanceof ItemSword || item instanceof ItemFood || item instanceof ItemPotion && !ItemPotion.isSplash(itemStack.getMetadata())) {
            return 1;
        }

        return -1;
    }

    private static class Buffer extends AbstractCheckBuffer { }

}
