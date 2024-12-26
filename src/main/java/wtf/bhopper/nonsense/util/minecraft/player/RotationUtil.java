package wtf.bhopper.nonsense.util.minecraft.player;

import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import wtf.bhopper.nonsense.util.minecraft.IMinecraft;
import wtf.bhopper.nonsense.util.misc.MathUtil;

import java.util.List;

public class RotationUtil implements IMinecraft {

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

    public static Vec3 getRotationVec(Rotation rotation) {
        return getRotationVec(rotation.yaw, rotation.pitch);
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

    public static Rotation lerp(Rotation start, Rotation end, float factor) {

        float startYaw = MathHelper.wrapAngleTo180_float(start.yaw);
        float endYaw = MathHelper.wrapAngleTo180_float(end.yaw);

        float delta = endYaw - startYaw;
        if (delta > 180.0F) {
            delta -= 360.0F;
        } else if (delta < -180.0F) {
            delta += 360.0F;
        }

        return new Rotation(
                MathHelper.wrapAngleTo180_float(startYaw + delta * factor),
                MathUtil.lerp(start.pitch, end.pitch, factor)
        );
    }

    public static boolean isOverBlock(Rotation rotation, BlockPos pos, EnumFacing facing) {
        MovingObjectPosition objectMouseOver = rayCastBlocks(rotation, 4.5F, mc.thePlayer);

        if (objectMouseOver == null || objectMouseOver.hitVec == null) {
            return false;
        }

        return objectMouseOver.getBlockPos().equals(pos) && objectMouseOver.sideHit == facing;
    }

    public static MovingObjectPosition rayCast(Rotation rotation, double range, Entity entity) {

        if (entity == null || mc.theWorld == null) {
            return null;
        }

        MovingObjectPosition objectMouseOver = entity.rayTraceCustom(range, rotation.yaw, rotation.pitch);
        Vec3 start = entity.getPositionEyes(1.0F);
        double fixedRange = objectMouseOver != null ? objectMouseOver.hitVec.distanceTo(start) : range;

        Vec3 look = entity.getVectorForRotation(rotation.pitch, rotation.yaw);
        Vec3 end = start.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);

        Entity pointedEntity = null;
        Vec3 hitVec = null;

        List<Entity> possibleEntities = mc.theWorld.getEntitiesInAABBexcluding(entity,
                entity.getEntityBoundingBox().addCoord(look.xCoord * range, look.yCoord * range, look.zCoord * range).expand(1.0F, 1.0F, 1.0F),
                Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));

        double reach = fixedRange;

        for (Entity e : possibleEntities) {
            float collisionBoarderSize = e.getCollisionBorderSize();
            AxisAlignedBB boundingBox = e.getEntityBoundingBox().expand(collisionBoarderSize, collisionBoarderSize, collisionBoarderSize);
            MovingObjectPosition intercept = boundingBox.calculateIntercept(start, end);

            if (boundingBox.isVecInside(start)) {
                if (reach >= 0.0) {
                    pointedEntity = e;
                    hitVec = intercept != null ? intercept.hitVec : start;
                    reach = 0.0;
                }
            } else if (intercept != null) {
                double distance = start.distanceTo(intercept.hitVec);

                if (distance < reach || reach == 0.0) {
                    pointedEntity = e;
                    hitVec = intercept.hitVec;
                    reach = distance;
                }
            }
        }

        if (pointedEntity != null && (reach < fixedRange || objectMouseOver == null)) {
            objectMouseOver = new MovingObjectPosition(pointedEntity, hitVec);
        }

        return objectMouseOver;
    }

    public static MovingObjectPosition rayCastBlocks(Rotation rotation, double range, Entity entity) {
        if (entity == null || mc.theWorld == null) {
            return null;
        }

        return entity.rayTraceCustom(range, rotation.yaw, rotation.pitch);
    }

    public static MovingObjectPosition rayCastEntity(Rotation rotation, double range, Entity entity) {
        if (entity == null || mc.theWorld == null) {
            return null;
        }

        MovingObjectPosition objectMouseOver = null;
        Vec3 start = entity.getPositionEyes(1.0F);

        Vec3 look = entity.getVectorForRotation(rotation.pitch, rotation.yaw);
        Vec3 end = start.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);

        Entity pointedEntity = null;
        Vec3 hitVec = null;

        List<Entity> possibleEntities = mc.theWorld.getEntitiesInAABBexcluding(entity,
                entity.getEntityBoundingBox().addCoord(look.xCoord * range, look.yCoord * range, look.zCoord * range).expand(1.0F, 1.0F, 1.0F),
                Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));

        double reach = range;

        for (Entity e : possibleEntities) {
            float collisionBoarderSize = e.getCollisionBorderSize();
            AxisAlignedBB boundingBox = e.getEntityBoundingBox().expand(collisionBoarderSize, collisionBoarderSize, collisionBoarderSize);
            MovingObjectPosition intercept = boundingBox.calculateIntercept(start, end);

            if (boundingBox.isVecInside(start)) {
                if (reach >= 0.0) {
                    pointedEntity = e;
                    hitVec = intercept != null ? intercept.hitVec : start;
                    reach = 0.0;
                }
            } else if (intercept != null) {
                double distance = start.distanceTo(intercept.hitVec);

                if (distance < reach || reach == 0.0) {
                    pointedEntity = e;
                    hitVec = intercept.hitVec;
                    reach = distance;
                }
            }
        }

        if (pointedEntity != null) {
            objectMouseOver = new MovingObjectPosition(pointedEntity, hitVec);
        }

        return objectMouseOver;
    }

}
