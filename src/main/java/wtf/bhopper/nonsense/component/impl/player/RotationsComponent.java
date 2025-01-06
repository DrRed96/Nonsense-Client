package wtf.bhopper.nonsense.component.impl.player;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.component.Component;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.EventPriorities;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventPreMotion;
import wtf.bhopper.nonsense.event.impl.player.EventUpdate;
import wtf.bhopper.nonsense.event.impl.player.movement.EventJump;
import wtf.bhopper.nonsense.event.impl.player.movement.EventMovementInput;
import wtf.bhopper.nonsense.event.impl.player.movement.EventStrafe;
import wtf.bhopper.nonsense.module.impl.movement.MovementFix;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;
import wtf.bhopper.nonsense.util.minecraft.player.Rotation;
import wtf.bhopper.nonsense.util.misc.MathUtil;

public class RotationsComponent extends Component {

    public float serverYaw = 0.0F;
    public float serverPitch = 0.0F;
    public float prevServerYaw = 0.0F;
    public float prevServerPitch = 0.0F;

    private boolean active = false;
    private boolean smoothed = false;
    private boolean updated = false;

    public static void updateServerRotations(Rotation rotation) {
        updateServerRotations(rotation.yaw, rotation.pitch);
    }

    public static void updateServerRotations(float yaw, float pitch) {
        RotationsComponent component = Nonsense.component(RotationsComponent.class);

        component.serverYaw = yaw;
        component.serverPitch = pitch;

        component.smooth();

        component.active = true;
        component.updated = true;
    }

    public static Vec3 getLook(float delta, Entity entity) {
        RotationsComponent component = Nonsense.component(RotationsComponent.class);

        if (delta == 1.0F) {
            return entity.getVectorForRotation(component.serverPitch, component.serverYaw);
        }

        float pitch = MathUtil.lerp(component.prevServerPitch, component.serverPitch, delta);
        float yaw = MathUtil.lerp(component.prevServerYaw, component.serverYaw, delta);
        return entity.getVectorForRotation(pitch, yaw);
    }

    @EventLink(EventPriorities.VERY_LOW)
    public final Listener<EventPreMotion> onPre = event -> {

        if (!this.updated) {
            this.active = false;
        }

        if (this.active) {
            event.yaw = mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset = this.serverYaw;
            event.pitch = mc.thePlayer.rotationPitchHead = this.serverPitch;

            if (Math.abs((this.serverYaw - mc.thePlayer.rotationYaw) % 360) < 1 && Math.abs((this.serverPitch - mc.thePlayer.rotationPitch)) < 1) {
                this.active = false;
            }

            this.prevServerYaw = this.serverYaw;
            this.prevServerPitch = this.serverPitch;

        } else {
            this.prevServerYaw = mc.thePlayer.rotationYaw;
            this.prevServerPitch = mc.thePlayer.rotationPitch;
        }

        this.updated = false;
        this.smoothed = false;

    };

    @EventLink(EventPriorities.VERY_HIGH)
    public final Listener<EventUpdate> onUpdateHigh = _ -> {
        this.serverYaw = mc.thePlayer.rotationYaw;
        this.serverPitch = mc.thePlayer.rotationPitch;
    };

    @EventLink(EventPriorities.VERY_LOW)
    public final Listener<EventUpdate> onUpdateLow = _ -> {

        if (!this.active) {
            this.serverYaw = this.prevServerYaw = mc.thePlayer.rotationYaw;
            this.serverPitch = this.prevServerPitch = mc.thePlayer.rotationPitch;
        } else {
            this.smooth();
        }
    };

    @EventLink(EventPriorities.VERY_LOW)
    public final Listener<EventStrafe> onStrafe = event -> {
        if (this.active && getMovementCorrection() != MovementCorrection.NONE) {
            event.yaw = this.serverYaw;
        }
    };

    @EventLink(EventPriorities.VERY_LOW)
    public final Listener<EventJump> onJump = event -> {
        if (this.active && getMovementCorrection() != MovementCorrection.NONE) {
            event.yaw = this.serverYaw;
        }
    };

    @EventLink(EventPriorities.LOW)
    public final Listener<EventMovementInput> onInput = event -> {
        if (this.active && getMovementCorrection() == MovementCorrection.OPTIMIZED) {
            fixYaw(event, this.serverYaw);
        }
    };

    private void smooth() {
        if (!this.smoothed) {
            if (getMovementCorrection() != MovementCorrection.NONE) {
                mc.thePlayer.movementYaw = this.serverYaw;
            }
            mc.thePlayer.velocityYaw = this.serverPitch;
            this.smoothed = true;
        }
    }

    public static void fixYaw(EventMovementInput event, float yaw) {
        float forward = event.forwards;
        float strafe = event.strafe;

        if (forward == 0.0F && strafe == 0.0F) {
            return;
        }

        double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(MoveUtil.direction(mc.thePlayer.rotationYaw, forward, strafe)));

        float closestForward = 0.0F;
        float closestStrafe = 0.0F;
        float closestDiff = Float.MAX_VALUE;

        for (float predForward = -1.0F; predForward <= 1.0F; predForward += 1.0F) {
            for (float predStrafe = -1.0F; predStrafe <= 1.0F; predStrafe += 1.0F) {

                if (predForward == 0.0F && predStrafe == 0.0F) {
                    continue;
                }

                double predAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(MoveUtil.direction(yaw, predForward, predStrafe)));
                double diff = MathUtil.wrappedDifference(angle, predAngle);

                if (diff < closestDiff) {
                    closestDiff = (float)diff;
                    closestForward = predForward;
                    closestStrafe = predStrafe;
                }

            }
        }

        event.forwards = closestForward;
        event.strafe = closestStrafe;
    }

    public static MovementCorrection getMovementCorrection() {
        MovementFix mod = Nonsense.module(MovementFix.class);
        if (!mod.isToggled()) {
            return MovementCorrection.NONE;
        }

        return mod.optimize.get() ? MovementCorrection.OPTIMIZED : MovementCorrection.NORMAL;

    }

    public enum MovementCorrection {
        NORMAL,
        OPTIMIZED,
        NONE
    }

}
