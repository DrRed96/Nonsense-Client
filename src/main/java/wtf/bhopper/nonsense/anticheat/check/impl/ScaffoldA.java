package wtf.bhopper.nonsense.anticheat.check.impl;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.util.BlockPos;
import wtf.bhopper.nonsense.anticheat.PlayerData;
import wtf.bhopper.nonsense.anticheat.check.AbstractCheck;
import wtf.bhopper.nonsense.anticheat.check.CheckInfo;
import wtf.bhopper.nonsense.anticheat.check.data.AbstractCheckBuffer;
import wtf.bhopper.nonsense.util.minecraft.world.BlockUtil;

@CheckInfo(name = "Scaffold", description = "Detects scaffold cheats.", maxVl = 5)
public class ScaffoldA extends AbstractCheck {


    @Override
    public void handleRelMove(PlayerData data, S14PacketEntity packet) {
        if (data.getDeltaXZ() >= 0.07) {
            data.getCheckData(Buffer.class, Buffer::new).speedTicks++;
        } else {
            data.getCheckData(Buffer.class, Buffer::new).speedTicks = 0;
        }

        if (data.isSneaking()) {
            data.getCheckData(Buffer.class, Buffer::new).sneakTicks++;
        } else {
            data.getCheckData(Buffer.class, Buffer::new).sneakTicks = 0;
        }

        if (Math.abs(data.getDeltaY()) > 0.1) {
            data.getCheckData(Buffer.class, Buffer::new).voidTicks++;
        } else {
            data.getCheckData(Buffer.class, Buffer::new).voidTicks = 0;
        }

        ItemStack heldItem = data.getEntity().getHeldItem();
        if (data.getLastSwing().passedTime() <= 500L &&
                data.getRotationPitch() >= 70.0F &&
                heldItem != null && heldItem.getItem() instanceof ItemBlock &&
                data.getCheckData(Buffer.class).speedTicks >= 20 &&
                data.getCheckData(Buffer.class).sneakTicks >= 30 &&
                data.getCheckData(Buffer.class).voidTicks >= 20) {
            BlockPos pos = data.getBlockPosition().down(2);
            boolean overAir = true;
            for (int i = 0; i < 4; i++) {
                if (!BlockUtil.isAir(pos)) {
                    overAir = false;
                    break;
                }
                pos = pos.down();
            }
            if (overAir) {
                data.incrementVl(this);
            }
        }
    }

    private static class Buffer extends AbstractCheckBuffer {
        private int speedTicks = 0;
        private int sneakTicks = 0;
        private int voidTicks = 0;
    }

}
