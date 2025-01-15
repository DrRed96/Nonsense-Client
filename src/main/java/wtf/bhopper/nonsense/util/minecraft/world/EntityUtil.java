package wtf.bhopper.nonsense.util.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.util.minecraft.IMinecraft;
import wtf.bhopper.nonsense.util.misc.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class EntityUtil implements IMinecraft {

    public static PathResult predictThrowablePath(double posX, double posY, double posZ, float rotationYaw, float rotationPitch, Entity thrower, int maxSimulations) {
        return predictProjectilePath(posX, posY, posZ, rotationYaw, rotationPitch, 0.25F, 1.5F, 0.03F, thrower, maxSimulations);
    }

    public static PathResult predictProjectilePath(double posX, double posY, double posZ, float rotationYaw, float rotationPitch, float size, float velocity, float gravity, Entity thrower, int maxSimulations) {

        double x = posX - MathHelper.cos(MathUtil.rad(rotationYaw)) * 0.16;
        double y = posY - 0.1;
        double z = posZ - MathHelper.sin(MathUtil.rad(rotationYaw)) * 0.16;

        double motionX = -MathHelper.sin(MathUtil.rad(rotationYaw)) * MathHelper.cos(MathUtil.rad(rotationPitch)) * 0.4;
        double motionY = -MathHelper.sin(MathUtil.rad(rotationPitch)) * 0.4;
        double motionZ = MathHelper.cos(MathUtil.rad(rotationYaw)) * MathHelper.cos(MathUtil.rad(rotationPitch)) * 0.4;

        float factor = velocity / MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX *= factor;
        motionY *= factor;
        motionZ *= factor;

        List<Vec3> points = new ArrayList<>();
        points.add(new Vec3(x, y, z));

        MovingObjectPosition intercept = null;

        for (int i = 0; i < maxSimulations; i++) {

            Vec3 start = new Vec3(x, y, z);
            Vec3 end = start.addVector(motionX, motionY, motionZ);
            MovingObjectPosition worldIntercept = mc.theWorld.rayTraceBlocks(start, end, false, true, false);

            start = new Vec3(x, y, z);
            end = start.addVector(motionX, motionY, motionZ);

            if (worldIntercept != null) {
                end = new Vec3(worldIntercept.hitVec.xCoord, worldIntercept.hitVec.yCoord, worldIntercept.hitVec.zCoord);
            }

            AxisAlignedBB boundingBox = new AxisAlignedBB(x - size / 2.0F, y, z - size / 2.0F, x + size / 2.0F, y + size, z + size / 2.0F);

            Entity entity = null;
            List<Entity> entities = mc.theWorld.getEntitiesWithinAABBExcludingEntity(thrower, boundingBox.expand(1.0, 1.0, 1.0));
            double reach = 0.0;

            for (Entity e : entities) {
                if (!e.canBeCollidedWith()) {
                    continue;
                }

                AxisAlignedBB entityBounds = e.getEntityBoundingBox().expand(0.3F, 0.3F, 0.3F);
                MovingObjectPosition entityIntercept = entityBounds.calculateIntercept(start, end);

                if (entityIntercept != null) {
                    double distance = start.squareDistanceTo(entityIntercept.hitVec);
                    if (distance < reach || reach == 0.0) {
                        entity = e;
                        reach = distance;
                    }
                }
            }

            if (entity != null) {
                worldIntercept = new MovingObjectPosition(entity);
            }

            points.add(end);

            if (worldIntercept != null && worldIntercept.typeOfHit != MovingObjectPosition.MovingObjectType.MISS) {
                intercept = worldIntercept;
                break;
            }

            x += motionX;
            y += motionY;
            z += motionZ;

            motionX *= 0.99;
            motionY *= 0.99;
            motionZ *= 0.99;

            motionY -= gravity;

        }

        return new PathResult(points, intercept);
    }

    public record PathResult(List<Vec3> path, MovingObjectPosition intercept) {}

}
