package wtf.bhopper.nonsense.util.minecraft;

import de.florianmichael.viamcp.fixes.AttackOrder;
import io.netty.buffer.Unpooled;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.EventPostClick;
import wtf.bhopper.nonsense.event.impl.EventPreClick;
import wtf.bhopper.nonsense.module.impl.other.Debugger;

public class PacketUtil implements MinecraftInstance {

    public static void send(Packet<?> packet) {
        mc.thePlayer.sendQueue.addToSendQueue(packet);
    }

    public static void sendNoEvent(Packet<?> packet) {
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
        Nonsense.getEventBus().post(new Debugger.EventPacketDebug(packet, Debugger.State.NO_EVENT, Debugger.EventPacketDebug.Direction.OUTGOING));
    }

    public static void leftClickPackets(MovingObjectPosition objectMouseOver, boolean swing) {

        EventPreClick event = new EventPreClick(EventPreClick.Button.LEFT, true, objectMouseOver);
        Nonsense.getEventBus().post(event);
        if (event.isCancelled()) {
            return;
        }

        if (mc.leftClickCounter <= 0) {

            PlayerUtil.swingConditional(!swing, objectMouseOver);

            if (event.mouseOver == null) {
                if (mc.playerController.isNotCreative()) {
                    mc.leftClickCounter = 10;
                }
                return;
            }

            switch (event.mouseOver.typeOfHit) {
                case ENTITY:
                    AttackOrder.sendFixedAttack(mc.thePlayer, event.mouseOver.entityHit);
                    break;

                case BLOCK:
                    BlockPos blockpos = event.mouseOver.getBlockPos();

                    if (mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                        mc.playerController.clickBlock(blockpos, event.mouseOver.sideHit);
                        break;
                    }

                case MISS:
                default:
                    if (mc.playerController.isNotCreative()) {
                        mc.leftClickCounter = 10;
                    }
            }

            Nonsense.getEventBus().post(new EventPostClick(EventPostClick.Button.LEFT, true, event.mouseOver));

        }
    }

    public static void rightClickPackets(MovingObjectPosition objectMouseOver, boolean swing, boolean bypass) {

        EventPreClick event = new EventPreClick(EventPreClick.Button.RIGHT, true, objectMouseOver);
        Nonsense.getEventBus().post(event);
        if (event.isCancelled()) {
            return;
        }

        if (!mc.playerController.isHittingBlock() || bypass) {

            mc.rightClickDelayTimer = 4;
            boolean flag = true;
            ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();

            if (event.mouseOver == null) {
                return;
            }
            switch (event.mouseOver.typeOfHit) {
                case ENTITY -> {
                    if (mc.playerController.isPlayerRightClickingOnEntity(mc.thePlayer, event.mouseOver.entityHit, event.mouseOver)) {
                        flag = false;
                    } else if (mc.playerController.interactWithEntitySendPacket(mc.thePlayer, event.mouseOver.entityHit)) {
                        flag = false;
                    }

                }

                case BLOCK -> {
                    BlockPos blockpos = event.mouseOver.getBlockPos();

                    if (mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {

                        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemstack, blockpos, event.mouseOver.sideHit, event.mouseOver.hitVec)) {
                            flag = false;
                            PlayerUtil.swing(!swing);
                        }

                        if (itemstack == null) {
                            return;
                        }

                        if (itemstack.stackSize == 0) {
                            mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem] = null;
                        }
                    }
                }
            }


            if (flag) {
                ItemStack itemstack1 = mc.thePlayer.inventory.getCurrentItem();

                if (itemstack1 != null && mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, itemstack1)) {
                    mc.entityRenderer.itemRenderer.resetEquippedProgress2();
                }
            }

            Nonsense.getEventBus().post(new EventPostClick(EventPostClick.Button.RIGHT, true, event.mouseOver));
        }
    }

    public static PacketBuffer createStringBuffer(String data) {
        return new PacketBuffer(Unpooled.buffer()).writeString(data);
    }

    public static PacketBuffer createByteBuffer(String data) {
        return new PacketBuffer(Unpooled.wrappedBuffer(data.getBytes()));
    }

}
