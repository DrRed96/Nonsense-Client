package wtf.bhopper.nonsense.util.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;
import wtf.bhopper.nonsense.util.misc.MathUtil;

import java.util.Set;

public class RotationUtil implements MinecraftInstance {

    public static float serverYaw = 0.0F;
    public static float serverPitch = 0.0F;
    public static float prevServerYaw = 0.0F;
    public static float prevServerPitch = 0.0F;

    public static void updateServerRotations(float yaw, float pitch) {
        prevServerYaw = serverYaw;
        prevServerPitch = serverPitch;
        serverYaw = mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset = yaw;
        serverPitch = mc.thePlayer.rotationPitchHead = pitch;
    }

    public static Rotation getRotations(double rotX, double rotY, double rotZ, double startX, double startY, double startZ) {
        double x = rotX - startX;
        double y = rotY - startY;
        double z = rotZ - startZ;
        double dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0F;
        float pitch = (float)(-(Math.atan2(y, dist) * 180.0 / Math.PI));
        return new Rotation(yaw, pitch, new Vec3(rotX, rotY, rotZ));
    }

    public static Rotation getRotations(double posX, double posY, double posZ) {
        return getRotations(posX, posY, posZ, mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    }

    public static Rotation getRotations(Vec3 vec) {
        return getRotations(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public static Rotation getRotations(BlockPos blockPos) {
        return getRotations(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
    }

    public static Rotation getRotations(BlockPos blockPos, EnumFacing facing) {
        return getRotations(getHitVec(blockPos, facing));
    }

    public static Rotation getRotations(Entity entity) {
        return getRotations(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
    }
    public static Rotation getRotations(Entity target, Entity entity) {
        return getRotations(target.posX, target.posY + target.getEyeHeight(), target.posZ, entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
    }

    public static Rotation getRotationsOptimized(AxisAlignedBB boundingBox) {
        double eyeX = mc.thePlayer.posX;
        double eyeY = mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight();
        double eyeZ = mc.thePlayer.posZ;
        return getRotations(MathUtil.closestPoint(boundingBox, eyeX, eyeY, eyeZ));
    }

    public static float rayCastRange(Vec3 pos, AxisAlignedBB boundingBox) {
        Vec3 closestPoint = MathUtil.closestPoint(boundingBox, pos);
        return (float)pos.distanceTo(closestPoint);
    }

    public static float rayCastRange(AxisAlignedBB boundingBox) {
        return rayCastRange(PlayerUtil.eyesPos(), boundingBox);
    }

    public static Vec3 getRotationVec(Entity entity, float delta) {
        return getRotationVec(entity.rotationYaw, entity.rotationPitch, entity.prevRotationYaw, entity.prevRotationPitch, delta);
    }

    public static Vec3 getRotationVec(float yaw, float pitch, float prevYaw, float prevPitch, float delta) {
        if (delta == 1.0F) {
            return getRotationVec(yaw, pitch);
        }

        float fixedYaw = MathUtil.lerp(prevYaw, yaw, delta);
        float fixedPitch = MathUtil.lerp(prevPitch, pitch, delta);
        return getRotationVec(fixedYaw, fixedPitch);
    }

    public static Vec3 getRotationVec(float yaw, float pitch) {
        float f = MathHelper.cos(-yaw * MathHelper.deg2Rad - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * MathHelper.deg2Rad - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * MathHelper.deg2Rad);
        float f3 = MathHelper.sin(-pitch * MathHelper.deg2Rad);
        return new Vec3(f1 * f2, f3, f * f2);
    }

    public static Vec3 getHitVec(BlockPos blockPos, EnumFacing facing) {
        return new Vec3(blockPos)
                .addVector(0.5, 0.5, 0.5)
                .add(new Vec3(
                        facing.getDirectionVec().getX() * 0.5,
                        facing.getDirectionVec().getY() * 0.5,
                        facing.getDirectionVec().getZ() * 0.5
                ));
    }

    public static Vec3 getHitVecOptimized(BlockPos blockPos, EnumFacing facing) {
        Vec3 eyes = PlayerUtil.eyesPos();
        double x, y, z;

        return MathUtil.closestPointOnFace(new AxisAlignedBB(blockPos, blockPos.add(1, 1, 1)), facing, eyes);
    }

    public static float getYawChange(double posX, double posZ) {
        double deltaX = posX - mc.thePlayer.posX;
        double deltaZ = posZ - mc.thePlayer.posZ;
        double yawToEntity;
        if (deltaZ < 0.0D && deltaX < 0.0D) {
            yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else if (deltaZ < 0.0D && deltaX > 0.0D) {
            yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else {
            yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }

        return MathHelper.wrapAngleTo180_float(-(mc.thePlayer.rotationYaw - (float)yawToEntity));
    }

    public static float getPitchChange(Entity entity, double posY) {
        double deltaX = entity.posX - mc.thePlayer.posX;
        double deltaZ = entity.posZ - mc.thePlayer.posZ;
        double deltaY = posY - 2.2D + (double)entity.getEyeHeight() - mc.thePlayer.posY;
        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationPitch - (float)pitchToEntity) - 2.5F;
    }

}
