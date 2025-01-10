package wtf.bhopper.nonsense.module.impl.movement;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.movement.EventMove;
import wtf.bhopper.nonsense.event.impl.player.EventPreMotion;
import wtf.bhopper.nonsense.event.impl.packet.EventReceivePacket;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;
import wtf.bhopper.nonsense.util.misc.Stopwatch;

@ModuleInfo(name = "Speed",
        description = "Increases your move speed.",
        category = ModuleCategory.MOVEMENT)
public class Speed extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Method for speed.", Mode.VANILLA);
    private final NumberProperty speedSet = new NumberProperty("Speed", "Move speed.", () -> this.mode.is(Mode.VANILLA), 1.0, 0.1, 3.0, 0.01);
    private final BooleanProperty jump = new BooleanProperty("Jump", "Automatically Jumps", false, () -> this.mode.isAny(Mode.VANILLA, Mode.MINIBLOX));
    private final NumberProperty bhopSpeed = new NumberProperty("Bhop Speed", "Speed for Bhop Mode.\n1.6 will bypass NCP", () -> this.mode.isAny(Mode.BHOP, Mode.LOW_HOP), 1.6, 0.0, 3.0, 0.01);
    private final NumberProperty jumpHeight = new NumberProperty("Jump Height", "Bhop jump height", () -> this.mode.is(Mode.BHOP), 0.4, 0.1, 1.0, 0.01);
    private final BooleanProperty bhopSlow = new BooleanProperty("Slow", "Slows you down more to help bypass", false, () -> this.mode.isAny(Mode.BHOP, Mode.LOW_HOP));
    private final BooleanProperty limit = new BooleanProperty("Limit Speed", "Limits your speed, useful for servers with strict anti-cheats.", false, () -> this.mode.isAny(Mode.BHOP, Mode.LOW_HOP));
    private final BooleanProperty damageBoost = new BooleanProperty("Damage Boost", "Boosts your speed when you get hit", false, () -> this.mode.is(Mode.BHOP));

    private double speed = 0.0;
    private double lastDist = 0.0;
    private int stage = 0;

    private boolean didLowHop = false;

    private final Stopwatch timer = new Stopwatch();

    public Speed() {
        this.addProperties(this.mode, this.speedSet, this.jump, this.bhopSpeed, this.jumpHeight, this.bhopSlow, this.limit);
        this.setSuffix(() -> {
            if (this.mode.is(Mode.BHOP) && this.bhopSlow.get()) {
                return "Bhop Slow";
            }

            return this.mode.getDisplayValue();
        });
    }

    @Override
    public void onEnable() {
        this.speed = 0.0;
        this.lastDist = 0.0;
        this.stage = 0;
        this.didLowHop = false;
    }

    @EventLink
    public final Listener<EventMove> onMove = event -> {

        if (Nonsense.module(Flight.class).isToggled()) {
            return;
        }

        switch (this.mode.get()) {
            case VANILLA -> {
                if (MoveUtil.isMoving()) {
                    MoveUtil.setSpeed(event, this.speedSet.getDouble());
                    if (this.jump.get() && mc.thePlayer.onGround) {
                        MoveUtil.vertical(event, MoveUtil.jumpHeight());
                    }
                }
            }

            case BHOP, LOW_HOP -> {

                switch (this.stage) {
                    case 0 -> {
                        if (MoveUtil.isMoving()) {
                            this.speed = MoveUtil.baseSpeed() * 1.18 - 0.01;
                            this.stage = 1;
                        }
                    }

                    case 1 -> {
                        if (MoveUtil.isMoving() && mc.thePlayer.onGround) {
                            if (this.mode.is(Mode.LOW_HOP)) {
                                if (mc.thePlayer.isPotionActive(Potion.jump) || mc.gameSettings.keyBindJump.isKeyDown()) {
                                    MoveUtil.vertical(event, MoveUtil.jumpHeight(MoveUtil.JUMP_HEIGHT));
                                    this.didLowHop = false;
                                } else {
                                    MoveUtil.vertical(event, 0.2);
                                    this.didLowHop = true;
                                }
                            } else {
                                MoveUtil.vertical(event, MoveUtil.jumpHeight(this.jumpHeight.getDouble()));
                            }
                            this.speed *= this.bhopSpeed.getDouble();
                            this.stage = 2;
                        }
                    }

                    case 2 -> {
                        if (this.mode.is(Mode.LOW_HOP) && !mc.thePlayer.isPotionActive(Potion.jump) && this.didLowHop) {
                            MoveUtil.vertical(event, -0.0784);
                            this.didLowHop = false;
                        }
                        this.speed = this.lastDist - (this.lastDist - MoveUtil.baseSpeed()) * (this.bhopSlow.get() ? 0.76 : 0.66);
                        this.stage = 3;
                    }

                    case 3 -> {
                        if (mc.thePlayer.isCollidedVertically || !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0)).isEmpty()) {
                            this.stage = 0;
                        }
                        this.speed = this.lastDist - this.lastDist / MoveUtil.NCP_FRICTION;
                    }

                }

                if (this.limit.get()) {
                    if (this.timer.hasReached(2500L)) {
                        this.timer.reset();
                    }

                    this.speed = Math.min(this.speed, this.timer.hasReached(1250L) ? 0.44 : 0.43);
                }

                this.speed = Math.max(this.speed, MoveUtil.baseSpeed());
                MoveUtil.setSpeed(event, this.speed);

            }

            case MINIBLOX -> {
                MoveUtil.setSpeed(event, 0.39); // Miniblox has a hard speed cap, this is about as fast as you can go without using a disabler
                if (this.jump.get() && mc.thePlayer.onGround && MoveUtil.isMoving()) {
                    MoveUtil.jump(event);
                }
            }
        }

    };

    @EventLink
    public final Listener<EventPreMotion> onPre = _ -> this.lastDist = MoveUtil.lastDistance();

    @EventLink
    public final Listener<EventReceivePacket> onReceivePacket = event -> {

        if (event.packet instanceof S12PacketEntityVelocity packet && this.damageBoost.get()) {
            switch (this.mode.get()) {
                case BHOP -> {
                    double velocity = Math.hypot(packet.getMotionX() / 8000.0, packet.getMotionZ() / 8000.0);
                    if (this.speed < velocity && !mc.thePlayer.onGround) {
                        this.speed = velocity;
                    }
                }
            }
        }
    };

    private enum Mode {
        VANILLA,
        BHOP,
        LOW_HOP,
        MINIBLOX
    }

}
