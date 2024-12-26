package wtf.bhopper.nonsense.anticheat.check.impl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.network.play.server.*;
import wtf.bhopper.nonsense.anticheat.PlayerData;
import wtf.bhopper.nonsense.anticheat.check.Check;
import wtf.bhopper.nonsense.anticheat.check.CheckInfo;
import wtf.bhopper.nonsense.anticheat.check.data.AbstractCheckBuffer;
import wtf.bhopper.nonsense.util.misc.Clock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@CheckInfo(name = "Auto-Block C",
        description = "Swing shouldn't have been allowed.",
        maxVl = 6)
public class AutoBlockC extends Check {

    @Override
    public void handleRelMove(PlayerData data, S14PacketEntity packet) {
        this.handle(data);
    }

    @Override
    public void handleTeleport(PlayerData data, S18PacketEntityTeleport packet) {
        this.handle(data);
    }

    private void handle(PlayerData data) {
        if (!data.hasCheckData(Buffer.class)) {
            return;
        }

        Buffer buffer = data.getCheckData(Buffer.class);
        if (buffer.swingList.isEmpty()) {
            return;
        }

        Iterator<Clock> it = buffer.swingList.iterator();
        while (it.hasNext()) {
            Clock clock = it.next();
            if (!clock.hasReached(140L)) {
                continue;
            }

            if (data.getUseClock() == null || !data.getUseClock().hasReached(clock.getTime())) {
                it.remove();
                return;
            }

            if (buffer.incrementBuffer() > 1) {
                data.incrementVl(this);
            }
            it.remove();
        }
    }

    @Override
    public void handleEntityMetadata(PlayerData data, S1CPacketEntityMetadata packet) {
        if (data.hasCheckData(Buffer.class) && packet.getMetadata() != null && !data.isUsingItem()) {
            data.getCheckData(Buffer.class).swingList.clear();
        }
    }

    @Override
    public void handleAnimation(PlayerData data, S0BPacketAnimation packet) {
        if (packet.getAnimationType() == 0) {
            if (data.isUsingItem() && this.isItemSupported(data.getEntity())) {
                Buffer buffer = data.getCheckData(Buffer.class, Buffer::new);
                buffer.swingList.add(new Clock());
            } else if (data.hasCheckData(Buffer.class)) {
                data.getCheckData(Buffer.class).decrementBuffer(0.5F);
            }
        }
    }

    @Override
    public void handleBlockiBreakAnim(PlayerData data, S25PacketBlockBreakAnim packet) {
        if (data.hasCheckData(Buffer.class)) {
            data.getCheckData(Buffer.class).swingList.clear();
        }
    }

    private boolean isItemSupported(EntityPlayer player) {
        ItemStack itemStack = player.getHeldItem();
        if (itemStack == null) {
            return false;
        }
        Item item = itemStack.getItem();
        return item == Items.bow || item instanceof ItemSword || item instanceof ItemFood || item instanceof ItemPotion && !ItemPotion.isSplash(itemStack.getMetadata());
    }

    private static final class Buffer extends AbstractCheckBuffer {
        private final List<Clock> swingList = new ArrayList<>();
    }

}
