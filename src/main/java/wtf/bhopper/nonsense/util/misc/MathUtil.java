package wtf.bhopper.nonsense.util.misc;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {

    public static double getIncremental(double val, double inc) {
        double inverse = 1.0 / inc;
        return (double)Math.round(val * inverse) / inverse;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static int lerp(int a, int b, float f) {
        return a + (int)(f * (float)(b - a));
    }

    public static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    public static double lerp(double a, double b, double f) {
        return a + f * (b - a);
    }

    public static int incrementTo(int current, int target, int speed) {
        if (current == target) {
            return current;
        }
        return current < target ? Math.min(current + speed, target) : Math.max(current - speed, target);
    }

    public static int calculateCompensation(int target, int current, int speed, float delta) {
        int diff = current - target;

        float deltaSpeed = speed * delta < 0.25F ? 0.5F : speed * delta;

        if (diff > speed) {
            int result = (int)(current - deltaSpeed);
            return Math.max(result, target);
        } else if (diff < -speed) {
            int result = (int)(current + deltaSpeed);
            return Math.min(result, target);
        }

        return current;
    }

    public static Vec3 closestPoint(AxisAlignedBB aabb, double x, double y, double z) {
        double closestX = MathHelper.clamp_double(x, aabb.minX, aabb.maxX);
        double closestY = MathHelper.clamp_double(y, aabb.minY, aabb.maxY);
        double closestZ = MathHelper.clamp_double(z, aabb.minZ, aabb.maxZ);
        return new Vec3(closestX, closestY, closestZ);
    }

    public static Vec3 closestPoint(AxisAlignedBB aabb, Vec3 pos) {
        return closestPoint(aabb, pos.xCoord, pos.yCoord, pos.zCoord);
    }

    public static Vec3 closestPointOnFace(AxisAlignedBB aabb, EnumFacing face, double x, double y, double z) {
        double closestX, closestY, closestZ;

        switch (face) {
            case DOWN, UP -> {
                closestX = Math.max(aabb.minX, Math.min(x, aabb.maxX));
                closestY = face == EnumFacing.DOWN ? aabb.minY : aabb.maxY;
                closestZ = Math.max(aabb.minZ, Math.min(z, aabb.maxZ));
            }
            case NORTH, SOUTH -> {
                closestX = Math.max(aabb.minX, Math.min(x, aabb.maxX));
                closestY = Math.max(aabb.minY, Math.min(y, aabb.maxY));
                closestZ = face == EnumFacing.NORTH ? aabb.minZ : aabb.maxZ;
            }
            case WEST, EAST -> {
                closestX = face == EnumFacing.WEST ? aabb.minX : aabb.maxX;
                closestY = Math.max(aabb.minY, Math.min(y, aabb.maxY));
                closestZ = Math.max(aabb.minZ, Math.min(z, aabb.maxZ));
            }
            default -> throw new IllegalArgumentException("Invalid face: " + face);
        }

        return new Vec3(closestX, closestY, closestZ);
    }

    public static Vec3 closestPointOnFace(AxisAlignedBB aabb, EnumFacing face, Vec3 vec) {
        return closestPointOnFace(aabb, face, vec.xCoord, vec.yCoord, vec.zCoord);
    }

}
