package wtf.bhopper.nonsense.module.impl.combat;

import net.minecraft.entity.EntityLivingBase;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.movement.EventMovementInput;
import wtf.bhopper.nonsense.event.impl.render.EventRenderWorld;
import wtf.bhopper.nonsense.event.impl.player.movement.EventSpeed;
import wtf.bhopper.nonsense.event.impl.player.EventUpdate;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;
import wtf.bhopper.nonsense.util.minecraft.player.RotationUtil;
import wtf.bhopper.nonsense.util.misc.MathUtil;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.RenderUtil;

@ModuleInfo(name = "Target Strafe", description = "Strafes around entities", category = ModuleCategory.COMBAT)
public class TargetStrafe extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Method for strafing", Mode.CIRCLE);
    private final NumberProperty range = new NumberProperty("Range", "Strafe range", 1.0, 0.1, 4.0, 0.05, NumberProperty.FORMAT_DISTANCE);
    private final BooleanProperty jump = new BooleanProperty("Jump", "Requires holding the jump button", true);

    private final GroupProperty render = new GroupProperty("Render", "Renders the target strafe path", this);
    private final BooleanProperty enableRender = new BooleanProperty("Enable", "Enables rendering", true);
    private final NumberProperty renderPoints = new NumberProperty("Points", "Amount of points to use in the rendering", 30, 3, 30, 1);
    private final BooleanProperty renderOutline = new BooleanProperty("Outline", "Renders an outline", true);

    private boolean strafing = false;
    private EntityLivingBase target = null;

    private int direction = -1;
    private int ticks = 0;

    public TargetStrafe() {
        this.render.addProperties(this.enableRender, this.renderPoints, this.renderOutline);
        this.addProperties(this.mode, this.range, this.jump, this.render);
    }

    @Override
    public void onEnable() {
        this.strafing = false;
        this.target = null;
    }

    @EventLink
    public final Listener<EventUpdate> onUpdate = _ -> {
        this.target = this.getTarget();
        if (mc.thePlayer.isCollidedHorizontally && this.ticks >= 4) {
            direction = -direction;
            this.ticks = 0;
        } else {
            this.ticks++;
        }
    };

    @EventLink
    public final Listener<EventSpeed> onSpeed = event -> {
        if (!this.canStrafe()) {
            this.strafing = false;
            return;
        }

        this.strafing = true;

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

    @EventLink
    public final Listener<EventMovementInput> onMovementInput = _ -> this.strafing = this.canStrafe();

    @EventLink
    public final Listener<EventRenderWorld> onRender3D = event -> {
        if (this.strafing && this.enableRender.get() && this.target != null) {

            double x = MathUtil.lerp(this.target.lastTickPosX, this.target.posX, event.delta);
            double y = MathUtil.lerp(this.target.lastTickPosY, this.target.posY, event.delta);
            double z = MathUtil.lerp(this.target.lastTickPosZ,this.target.posZ, event.delta);

            if (this.renderOutline.get()) {
                RenderUtil.drawRadius(x, y, z, this.range.getDouble(), this.renderPoints.getInt(), 4.0F, ColorUtil.BLACK);
            }
            RenderUtil.drawRadius(x, y, z, this.range.getDouble(), this.renderPoints.getInt(), 2.0F, Hud.color());
        }
    };

    private EntityLivingBase getTarget() {
        return Nonsense.module(KillAura.class).getTarget();
    }

    private boolean canStrafe() {
        if (this.jump.get()) {
            if (!mc.thePlayer.movementInput.jump) {
                return false;
            }
        }

        return MoveUtil.isMoving() && this.target != null;
    }

    private enum Mode {
        CIRCLE,
        BACK
        // TODO: add Adaptive mode
    }

}
