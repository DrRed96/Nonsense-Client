package wtf.bhopper.nonsense.util.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;

import java.util.List;

public class PlayerUtil implements MinecraftInstance {

    public static boolean canUpdate() {
        return mc.thePlayer != null && mc.theWorld != null;
    }

    public static Vec3 eyesPos() {
        return new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    }

    public static void swing(boolean silent) {
        if (silent) {
            PacketUtil.send(new C0APacketAnimation());
        } else {
            mc.thePlayer.swingItem();
        }
    }

    public static void swingConditional(boolean silent, MovingObjectPosition mop) {
        if (mop != null && mop.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
            swing(silent);
        }
    }

    public static boolean isOnSameTeam(final EntityPlayer player) {

        if (player == null) {
            return false;
        }

        if (player.getTeam() != null && mc.thePlayer.getTeam() != null) {
            final char c1 = player.getDisplayName().getFormattedText().charAt(1);
            final char c2 = mc.thePlayer.getDisplayName().getFormattedText().charAt(1);
            return c1 == c2;
        }
        return false;
    }

    public static boolean isBlockUnder(final double height) {
        return isBlockUnder(height, true);
    }

    public static boolean isBlockUnder(final double height, final boolean boundingBox) {
        if (boundingBox) {
            for (int offset = 0; offset < height; offset += 2) {
                final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);

                if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                    return true;
                }
            }
        } else {
            for (int offset = 0; offset < height; offset++) {
                if (BlockUtil.getRelativeBlock(0, -offset, 0).isFullBlock()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isBlockUnder() {
        return isBlockUnder(mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
    }

    public static boolean isInLiquid() {
        return mc.thePlayer.isInWater() || mc.thePlayer.isInLava();
    }

    public static boolean isOnLiquid() {
        boolean onLiquid = false;
        final AxisAlignedBB playerBB = mc.thePlayer.getEntityBoundingBox();
        final int y = (int) playerBB.offset(0.0, -0.01, 0.0).minY;
        for (int x = MathHelper.floor_double(playerBB.minX); x < MathHelper.floor_double(playerBB.maxX) + 1; ++x) {
            for (int z = MathHelper.floor_double(playerBB.minZ); z < MathHelper.floor_double(playerBB.maxZ) + 1; ++z) {
                final Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && !(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }

    public static boolean isInsideBlock() {
        for (int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int y = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY); y < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxY) + 1; ++y) {
                for (int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
                    Block block = mc.thePlayer.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block != null && !(block instanceof BlockAir)) {
                        AxisAlignedBB boundingBox = block.getCollisionBoundingBox(mc.theWorld, new BlockPos(x, y, z), mc.theWorld.getBlockState(new BlockPos(x, y, z)));

                        if (block instanceof BlockHopper) {
                            boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
                        }

                        if (boundingBox != null && mc.thePlayer.getEntityBoundingBox().intersectsWith(boundingBox)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static double getMaxFallDist() {
        double fallDistance = 3.1;
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            final int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            fallDistance += (float) (amplifier + 1);
        }
        return fallDistance;
    }

    public static boolean selfDamage(double value, final boolean groundCheck, final boolean hurtTimeCheck) {
        if (groundCheck && !mc.thePlayer.onGround) {
            return false;
        }

        if (hurtTimeCheck && mc.thePlayer.hurtTime > 0) {
            return false;
        }

        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        int packetCount = (int)Math.ceil(getMaxFallDist() / value);
        for (int i = 0; i < packetCount; i++) {
            PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + value, z, false));
            PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
        }

        PacketUtil.sendNoEvent(new C03PacketPlayer(true));
        return true;
    }

    public static boolean selfDamageLow() {

        if (!mc.thePlayer.onGround || mc.thePlayer.hurtTime > 0) {
            return false;
        }

        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        for (int i = 0; (double)i < getMaxFallDist() / 0.0551 + 1.0; ++i) {
            PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0601, z, false));
            PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 5.0E-4, z, false));
            PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.005 + 6.01E-8, z, false));
        }

        PacketUtil.sendNoEvent(new C03PacketPlayer(true));

        return true;
    }

    public static boolean selfDamageJump() {

        if (!mc.thePlayer.onGround || mc.thePlayer.hurtTime > 0) {
            return false;
        }

        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        MoveUtil.JumpOffsets jumpOffsets = MoveUtil.getJumpOffsets(0.42);

        for (int i = 0; (double)i < getMaxFallDist() / jumpOffsets.maxHeight(); ++i) {
            for (double offset : jumpOffsets.offsets()) {
                PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + offset, z, false));
            }
        }

        PacketUtil.sendNoEvent(new C03PacketPlayer(true));

        return true;
    }


}
