package wtf.bhopper.nonsense.module.impl.combat;

import net.minecraft.entity.EntityLivingBase;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventSpeed;
import wtf.bhopper.nonsense.event.impl.EventUpdate;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.MoveUtil;
import wtf.bhopper.nonsense.util.minecraft.RotationUtil;

@ModuleInfo(name = "Target Strafe", description = "Strafes around entities", category = ModuleCategory.COMBAT)
public class TargetStrafe extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Method for strafing", Mode.CIRCLE);
    private final NumberProperty range = new NumberProperty("Range", "Strafe range", 1.0, 0.1, 4.0, 0.05, NumberProperty.FORMAT_DISTANCE);
    private final BooleanProperty jump = new BooleanProperty("Jump", "Requires holding the jump button", true);

    private int direction = -1;
    private int ticks = 0;

    public TargetStrafe() {
        this.autoAddProperties();
    }

    @EventLink
    public final Listener<EventUpdate> onUpdate = _ -> {
        if (mc.thePlayer.isCollidedHorizontally && this.ticks >= 4) {
            direction = -direction;
            this.ticks = 0;
        } else {
            this.ticks++;
        }
    };

    @EventLink
    public final Listener<EventSpeed> onSpeed = event -> {
        EntityLivingBase target = Nonsense.module(KillAura.class).getTarget();
        if (target == null || !this.canStrafe()) {
            return;
        }

        switch (this.mode.get()) {
            case CIRCLE -> {
                event.yaw = RotationUtil.getRotations(target).yaw;
                event.forward = mc.thePlayer.getDistanceToEntityXZ(target) > this.range.getFloat() ? 1.0 : 0.0;
                event.strafe = this.direction;
            }
            case BACK -> {
                event.yaw = RotationUtil.getRotations(target).yaw;
                event.forward = mc.thePlayer.getDistanceToEntityXZ(target) > this.range.getFloat() ? 1.0 : 0.0;
                event.strafe = RotationUtil.getRotations(mc.thePlayer, target).yaw - target.rotationYaw  > 0.0F ? 1.0 : -1.0;
            }
        }
    };

    private boolean canStrafe() {
        if (this.jump.get()) {
            if (!mc.thePlayer.movementInput.jump) {
                return false;
            }
        }

        return MoveUtil.isMoving();
    }

    private enum Mode {
        CIRCLE,
        BACK
        // TODO: add Adaptive mode
    }

}
