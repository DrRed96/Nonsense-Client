package wtf.bhopper.nonsense.util.minecraft;

import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.EventMove;
import wtf.bhopper.nonsense.event.impl.EventSlowDown;
import wtf.bhopper.nonsense.event.impl.EventSpeed;

public class MoveUtil implements MinecraftInstance {

    public static boolean isMoving() {
        return mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F;
    }

    public static double baseSpeed() {
        double baseSpeed = mc.thePlayer.capabilities.getWalkSpeed() * 2.873;
        if (mc.thePlayer.isPotionActive(Potion.moveSlowdown)) {
            baseSpeed /= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getAmplifier() + 1);
        }
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }

    public static double getSpeed() {
        double x = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double z = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        return MathHelper.sqrt_double(x * x + z * z);
    }

    public static void setSpeed(double speed) {
        setSpeed(null, speed);
    }

    public static void setSpeed(EventMove event, double speed) {
        setSpeed(event, speed, mc.thePlayer.rotationYaw, mc.thePlayer.moveForward, mc.thePlayer.moveStrafing);
    }

    public static void setSpeed(EventMove event, double speedIn, float yawIn, double forwardIn, double strafeIn) {

        EventSpeed eventSpeed = new EventSpeed(speedIn, yawIn, forwardIn, strafeIn);
        Nonsense.getEventBus().post(eventSpeed);

        double speed = eventSpeed.speed;
        float yaw = eventSpeed.yaw;
        double forward = eventSpeed.forward;
        double strafe = eventSpeed.strafe;

        if (mc.thePlayer.isUsingItem() && !mc.thePlayer.isRiding()) {
            EventSlowDown eventSlowDown = new EventSlowDown(0.2F);
            Nonsense.getEventBus().post(eventSlowDown);
            if (!eventSlowDown.isCancelled()) {
                speed *= eventSlowDown.factor;
            }
        }

        if (forward == 0.0 && strafe == 0.0) {
            setMotion(event, 0.0, 0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45.0F : 45.0F);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45.0F : -45.0F);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
        }

        double mx = Math.cos(Math.toRadians(yaw + 90.0F));
        double mz = Math.sin(Math.toRadians(yaw + 90.0F));

        setMotion(event, forward * speed * mx + strafe * speed * mz, forward * speed * mz - strafe * speed * mx);
    }

    public static void setMotion(EventMove event, double x, double z) {
        mc.thePlayer.motionX = x;
        mc.thePlayer.motionZ = z;
        if (event != null) {
            event.x = x;
            event.z = z;
        }
    }

    public static void vertical(EventMove event, double speed) {
        mc.thePlayer.motionY = speed;
        if (event != null) {
            event.y = speed;
        }
    }

    public static void vertical(double speed) {
        setSpeed(null, speed);
    }

    public static void jump(EventMove event, double motion) {
        vertical(event, jumpHeight(motion));
    }

    public static void jump(double motion) {
        vertical(jumpHeight(motion));
    }

    public static double jumpHeight(double height) {
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            return height + (amplifier + 1) * 0.1F;
        }
        return height;
    }

    public static double lastDistance() {
        double diffX = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double diffZ = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;

        return Math.sqrt(diffX * diffX + diffZ * diffZ);
    }

}
