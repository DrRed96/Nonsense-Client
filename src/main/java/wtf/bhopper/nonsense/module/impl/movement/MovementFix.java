package wtf.bhopper.nonsense.module.impl.movement;

import net.minecraft.util.MathHelper;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventMovementInput;
import wtf.bhopper.nonsense.event.impl.EventStrafe;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.util.minecraft.MoveUtil;
import wtf.bhopper.nonsense.util.minecraft.RotationUtil;
import wtf.bhopper.nonsense.util.misc.MathUtil;

@ModuleInfo(name = "Movement Fix",
        description = "Updates your movement based on your server rotations.",
        category = ModuleCategory.MOVEMENT)
public class MovementFix extends Module {

    private final BooleanProperty optimize = new BooleanProperty("Optimize", "Optimizes your movement to be closer to the client yaw", true);

    public MovementFix() {
        this.autoAddProperties();
    }

    @EventLink
    public final Listener<EventStrafe> onStrafe = event -> event.yaw = RotationUtil.serverYaw;

    @EventLink
    public final Listener<EventMovementInput> onMovementInput = event -> {
        if (this.optimize.get()) {

            final double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(MoveUtil.direction(mc.thePlayer.rotationYaw, event.forwards, event.strafe)));

            if (event.forwards == 0.0F && event.strafe == 0.0F) {
                return;
            }

            float closestForward = 0.0F;
            float closestStrafe = 0.0F;
            float closestDifference = Float.MAX_VALUE;

            for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
                for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                    if (predictedStrafe == 0 && predictedForward == 0) {
                        continue;
                    }

                    final double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(MoveUtil.direction(RotationUtil.serverYaw, predictedForward, predictedStrafe)));
                    final double difference = MathUtil.wrappedDifference(angle, predictedAngle);

                    if (difference < closestDifference) {
                        closestDifference = (float) difference;
                        closestForward = predictedForward;
                        closestStrafe = predictedStrafe;
                    }
                }
            }

            event.forwards = closestForward;
            event.strafe = closestStrafe;
        }
    };

}
