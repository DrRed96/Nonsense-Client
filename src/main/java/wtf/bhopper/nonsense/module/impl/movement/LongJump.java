package wtf.bhopper.nonsense.module.impl.movement;

import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventMove;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.minecraft.MoveUtil;

@ModuleInfo(name = "Long Jump", description = "Allows you to jump far", category = ModuleCategory.MOVEMENT)
public class LongJump extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Method for long jump", Mode.VANILLA);

    private boolean jumping = false;

    private int stage = 0;
    private double speed = 0.0;
    private double lastDist = 0.0;

    private int groundTicks = 1;

    public LongJump() {
        this.addProperties(this.mode);
    }

    @Override
    public void onEnable() {
        this.jumping = false;
        this.stage = 0;
        this.speed = 0.0;
        this.lastDist = 0.0;
        this.groundTicks = 1;
    }

    @EventLink
    public final Listener<EventPreMotion> onPre = event -> {

        switch (this.mode.get()) {
            case NCP -> {
                if (MoveUtil.onGround()) {
                    event.y += 7.435E-4;

                    if (MoveUtil.isMoving() && this.stage > 2) {
                        this.groundTicks++;
                    }
                }

                if (this.groundTicks > 1) {
                    this.toggle(false);
                }
            }
        }

        this.lastDist = MoveUtil.lastDistance();
    };

    @EventLink
    public final Listener<EventMove> onMove = event -> {
        switch (this.mode.get()) {
            case VANILLA -> {
                if (this.jumping) {
                    if (mc.thePlayer.onGround) {
                        this.toggle(false);
                    } else {
                        MoveUtil.setSpeed(event, 1.0);
                    }
                } else if (mc.thePlayer.onGround) {
                    this.jumping = true;
                    MoveUtil.setSpeed(event, 1.0);
                    MoveUtil.vertical(event, 0.42);
                }
            }

            case NCP -> {
                if (MoveUtil.isMoving()) {
                    switch (this.stage) {
                        case 0, 1 -> this.speed = 0.0;

                        case 2 -> {
                            if (MoveUtil.onGround()) {
                                MoveUtil.jump(event, 0.4);
                                this.speed = MoveUtil.baseSpeed() * 2.0;
                            }
                        }

                        case 3 -> this.speed = MoveUtil.baseSpeed() * 2.149;

                        case 4 -> { }

                        default -> {
                            if (mc.thePlayer.motionY < 0.0) {
                                mc.thePlayer.motionY *= 0.5;
                            }

                            this.speed = this.lastDist - this.lastDist / MoveUtil.SLOWDOWN_FACTOR;
                        }

                    }

                    this.speed = Math.max(this.speed, MoveUtil.baseSpeed());
                    this.stage++;
                }

                MoveUtil.setSpeed(event, this.speed);
            }
        }
    };

    @Override
    public String getSuffix() {
        return this.mode.getDisplayValue();
    }

    private enum Mode {
        VANILLA,
        @DisplayName("NCP") NCP
    }

}
