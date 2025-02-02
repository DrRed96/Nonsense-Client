package wtf.bhopper.nonsense.util.minecraft.player;

import net.minecraft.potion.Potion;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.player.movement.EventMove;
import wtf.bhopper.nonsense.event.impl.player.movement.EventSlowDown;
import wtf.bhopper.nonsense.event.impl.player.movement.EventSpeed;
import wtf.bhopper.nonsense.module.impl.movement.NoSlow;
import wtf.bhopper.nonsense.module.impl.movement.Terrain;
import wtf.bhopper.nonsense.util.minecraft.IMinecraft;

import java.util.ArrayList;
import java.util.List;

public class MoveUtil implements IMinecraft {

    public static final double WALK_SPEED = 2.221;
    public static final double SPRINT_MOD = 1.3;
    public static final double SNEAK_MOD = 0.3;
    public static final double ICE_MOD = 2.5;
    public static final double WEB_MOD = 0.105 / WALK_SPEED;
    public static final double SWIM_MOD = 0.520362;

    public static final double AIR_FRICTION = 0.98;
    public static final double WATER_FRICTION = 0.89;
    public static final double LAVA_FRICTION = 0.535;
    public static final double NCP_FRICTION = 159.0;

    public static final double JUMP_HEIGHT = 0.42;

    public static boolean isMoving() {
        return mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F;
    }

    public static boolean isMoving(float factor) {
        return mc.thePlayer.movementInput.moveForward > factor || mc.thePlayer.movementInput.moveForward < -factor ||
                mc.thePlayer.movementInput.moveStrafe > factor || mc.thePlayer.movementInput.moveStrafe < -factor;
    }

    public static boolean canSprint(boolean omni) {
        return omni ? isMoving(0.8F) : (mc.thePlayer.moveForward > 0.8F &&
                !mc.thePlayer.isSneaking() &&
                (mc.thePlayer.getFoodStats().getFoodLevel() >= 6 || mc.thePlayer.capabilities.allowFlying) &&
                !mc.thePlayer.isCollidedHorizontally &&
                !mc.thePlayer.isPotionActive(Potion.moveSlowdown) &&
                (!mc.thePlayer.isUsingItem() || Nonsense.module(NoSlow.class).canSprint()));
    }

    public static double baseSpeed() {
        double baseSpeed = mc.thePlayer.capabilities.getWalkSpeed() * WALK_SPEED;

        if (mc.thePlayer.isInWeb && !Nonsense.module(Terrain.class).cobwebs()) {
            baseSpeed *= WEB_MOD;

        } else if (PlayerUtil.isInLiquid()) {
            baseSpeed *= SWIM_MOD;

        } else if (mc.thePlayer.isSneaking()) {
            baseSpeed *= SNEAK_MOD;

        } else {
            if (canSprint(true)) {
                baseSpeed *= SPRINT_MOD;
            }

            if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                baseSpeed *= 1 + (0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1));
            }

            if (mc.thePlayer.isPotionActive(Potion.moveSlowdown)) {
                baseSpeed *= 0.29;
            }

        }

        return baseSpeed;
    }

    public static double baseSpeedStrafe() {
        double baseSpeed = WALK_SPEED;

        if (mc.thePlayer.isInWeb && !Nonsense.module(Terrain.class).cobwebs()) {
            baseSpeed *= WEB_MOD;

        } else if (PlayerUtil.isInLiquid()) {
            baseSpeed *= SWIM_MOD;

        } else if (mc.thePlayer.isSneaking()) {
            baseSpeed *= SNEAK_MOD;

        } else {
            if (canSprint(true)) {
                baseSpeed *= SPRINT_MOD;
            }

            if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                baseSpeed *= 1 + (0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1));
            }

            if (mc.thePlayer.isPotionActive(Potion.moveSlowdown)) {
                baseSpeed *= 0.29;
            }

        }

        return baseSpeed;
    }

    public static double getSpeed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public static double getSpeed(EventMove event) {
        return Math.hypot(event.x, event.z);
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

    public static Vec3 simulateSpeed(double speedIn, float yawIn, double forwardIn, double strafeIn, boolean event) {
        EventSpeed eventSpeed = new EventSpeed(speedIn, yawIn, forwardIn, strafeIn);
        if (event) {
            Nonsense.getEventBus().post(eventSpeed);
        }

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
            return new Vec3(0.0, 0.0, 0.0);
        }

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


        double mx = Math.cos(Math.toRadians(yaw + 90.0F));
        double mz = Math.sin(Math.toRadians(yaw + 90.0F));

        return new Vec3(forward * speed * mx + strafe * speed * mz, 0.0, forward * speed * mz - strafe * speed * mx);
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

    public static void jump(EventMove event) {
        jump(event, JUMP_HEIGHT);
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

    public static double jumpHeight() {
        return jumpHeight(JUMP_HEIGHT);
    }

    public static double lastDistance() {
        double diffX = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double diffZ = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;

        return Math.sqrt(diffX * diffX + diffZ * diffZ);
    }

    public static boolean onGround() {
        return mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically;
    }

    public static JumpOffsets getJumpOffsets(double jumpHeight) {
        List<Double> result = new ArrayList<>();

        double motion = jumpHeight;
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            motion += (float) (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
        }
        double y = motion;

        result.add(y);

        double maxHeight = 0.0;

        for (; ; ) {
            motion = (motion - 0.08) * 0.98;
            y += motion;

            if (y > maxHeight) {
                maxHeight = y;
            }

            if (y <= 0.0) {
                result.add(0.0);
                break;
            }

            result.add(y);
        }

        return new JumpOffsets(result, maxHeight);
    }

    public static float[] calculateForwardStrafe(double motionX, double motionZ, float yaw) {
        float x = (float) Math.sin(yaw * Math.PI / 180.0F);
        float z = (float) Math.cos(yaw * Math.PI / 180.0F);

        // Solve for strafe and forward
        double denominator = z * z + x * x; // Should be close to 1
        double strafe = (motionX * z + motionZ * x) / denominator;
        double forward = (motionZ * z - motionX * x) / denominator;

        return new float[]{(float) forward, (float) strafe};
    }

    public static double direction(float rotationYaw, final double moveForward, final double moveStrafing) {

        float yaw = rotationYaw;

        if (moveForward < 0.0F) {
            yaw += 180.0F;
        }

        float forward = 1.0F;

        if (moveForward < 0.0F) {
            forward = -0.5F;
        } else if (moveForward > 0.0F) {
            forward = 0.5F;
        }

        if (moveStrafing > 0.0F) {
            yaw -= 90.0F * forward;
        }
        if (moveStrafing < 0.0F) {
            yaw += 90.0F * forward;
        }

        return Math.toRadians(yaw);
    }

    public static double getPosYForJumpTick(int tick) {
        return switch (tick) {
            case 1 -> 0.42;
            case 2 -> 0.7532;
            case 3 -> 1.00133597911214;
            case 4 -> 1.16610926093821;
            case 5, 6 -> 1.24918707874468;
            case 7 -> 1.1707870772188;
            case 8 -> 1.0155550727022;
            case 9 -> 0.78502770378923;
            case 10 -> 0.48071087633169;
            case 11 -> 0.10408037809304;
            default -> 0.0;
        };
    }

    public record JumpOffsets(List<Double> offsets, double maxHeight) {
    }

}
