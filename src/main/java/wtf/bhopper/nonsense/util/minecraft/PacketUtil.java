package wtf.bhopper.nonsense.util.minecraft;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

public class PacketUtil implements MinecraftInstance {

    public static void send(Packet<?> packet) {
        mc.thePlayer.sendQueue.addToSendQueue(packet);
    }

    public static void sendNoEvent(Packet<?> packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static void rightClickPackets() {
        rightClickPackets(mc.objectMouseOver);
    }

    public static void rightClickPackets(MovingObjectPosition objectMouseOver) {
        if (objectMouseOver == null) {
            return;
        }

        ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();
        boolean flag = true;

        switch (objectMouseOver.typeOfHit) {
            case ENTITY:
                if (mc.playerController.isPlayerRightClickingOnEntity(mc.thePlayer, objectMouseOver.entityHit, objectMouseOver)) {
                    flag = false;
                } else if (mc.playerController.interactWithEntitySendPacket(mc.thePlayer, objectMouseOver.entityHit)) {
                    flag = false;
                }
                break;

            case BLOCK:
                BlockPos blockpos = objectMouseOver.getBlockPos();

                if (mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                    if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemstack, blockpos, objectMouseOver.sideHit, objectMouseOver.hitVec)) {
                        send(new C0APacketAnimation());
                        flag = false;
                    }

                    if (itemstack == null) {
                        return;
                    }

                }
                break;
        }

        if (flag) {
            ItemStack itemstack1 = mc.thePlayer.inventory.getCurrentItem();

            if (itemstack1 != null) {
                mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, itemstack1);
            }
        }
    }

}
