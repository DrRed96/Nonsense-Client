package wtf.bhopper.nonsense.module.impl.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.movement.EventMove;
import wtf.bhopper.nonsense.event.impl.player.EventPreMotion;
import wtf.bhopper.nonsense.event.impl.packet.EventReceivePacket;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.annotations.Description;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.misc.Stopwatch;

@ModuleInfo(name = "Flight",
        description = "Allows you to fly.",
        category = ModuleCategory.MOVEMENT,
        searchAlias = "fly")
public class Flight extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Flight method.", Mode.VANILLA);

    private final NumberProperty speedSet = new NumberProperty("Speed", "Flight speed", () -> this.mode.isAny(Mode.BOOST), 1.0, 0.1, 10.0, 0.01);
    private final NumberProperty hSpeed = new NumberProperty("Horizontal", "Horizontal Speed", () -> this.mode.isAny(Mode.VANILLA, Mode.MINIBLOX), 1.0, 0.1, 10.0, 0.01);
    private final NumberProperty vSpeed = new NumberProperty("Vertical", "Horizontal Speed", () -> this.mode.isAny(Mode.VANILLA, Mode.MINIBLOX), 0.5, 0.1, 10.0, 0.01);

    private final GroupProperty boostGroup = new GroupProperty("Boost", "Boost settings", this, () -> this.mode.isAny(Mode.BOOST));
    private final BooleanProperty useBoost = new BooleanProperty("Enable", "Enables boost fly.", false, () -> false);
    private final EnumProperty<Timer> useTimer = new EnumProperty<>("Timer", "Use timer to increase speed", Timer.NONE);
    private final NumberProperty timerFactor = new NumberProperty("Timer Factor", "Timer speed", () -> !useTimer.is(Timer.NONE), 1.5, 0.05, 5.0, 0.05);
    private final NumberProperty timerTime = new NumberProperty("Timer Time", "How long to use timer for", () -> useTimer.is(Timer.ON_BOOST), 500, 1, 3000, 1, NumberProperty.FORMAT_MS);
    private final EnumProperty<Damage> damage = new EnumProperty<>("Damage", "Causes damage which can disable speed checks", Damage.PACKET);
    private final NumberProperty timerStart = new NumberProperty("Timer Start", "Timer start factor.\nCan be set lower to help bypass timer checks when using damage.", 1.0, 0.01, 3.0, 0.01);
    private final BooleanProperty pushUp = new BooleanProperty("Push Up", "Pushes you up when you start flying", true);
    private final BooleanProperty quickStop = new BooleanProperty("Quick Stop", "Set motion to 0 when you stop flying", true);

    private final NumberProperty viewBobbing = new NumberProperty("View Bobbing", "View bobbing while flying", 0.0, 0.0, 1.0, 0.05);


    private int ticks = 0;

    private int stage = 0;
    private double speed = 0.0;
    private double lastDist = 0.0;

    private boolean stopTimer = false;
    private final Stopwatch timerClock = new Stopwatch();

    public Flight() {
        this.boostGroup.addProperties(this.useBoost, this.useTimer, this.timerFactor, this.timerTime, this.damage, this.timerStart, this.pushUp, this.quickStop);
        this.addProperties(this.mode, this.speedSet, this.hSpeed, this.vSpeed, this.boostGroup, this.viewBobbing);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @Override
    public void onEnable() {
        this.ticks = 0;
        this.stage = 0;
        this.speed = 0.0;
        this.lastDist = 0.0;
    }

    @Override
    public void onDisable() {

        switch (this.mode.get()) {
            case VANILLA -> mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
            case MINIBLOX -> {
                mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
                if (mc.thePlayer.motionY > 0.0) {
                    mc.thePlayer.motionY = 0.0;
                }
            }
            case BOOST -> {
                mc.timer.timerSpeed = 1.0F;

                if (this.quickStop.get()) {
                    mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
                }

            }

        }
    }

    @EventLink
    public final Listener<EventTick> onTick = _ -> {
        if (!PlayerUtil.canUpdate()) {
            return;
        }

        switch (this.mode.get()) {
            case MINIBLOX -> {
                this.ticks++;
                if (this.ticks == 6) {
                    PacketUtil.send(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SLEEPING));
                }
            }
        }
    };

    @EventLink
    public final Listener<EventMove> onMove = event -> {
        switch (this.mode.get()) {
            case VANILLA -> {
                MoveUtil.setSpeed(event, this.hSpeed.getDouble());
                if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                    MoveUtil.vertical(event, vSpeed.getDouble());
                } else if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                    MoveUtil.vertical(event, -vSpeed.getDouble());
                } else {
                    MoveUtil.vertical(event, 0.0);
                }
            }

            case NCP_GLIDE -> {
                MoveUtil.vertical(event, -0.0222);
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(
                        mc.thePlayer.posX + mc.thePlayer.motionX,
                        mc.thePlayer.posY + mc.thePlayer.motionY,
                        mc.thePlayer.posZ + mc.thePlayer.motionZ,
                        false
                ));
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
            }

            case MINIBLOX -> {
                if (this.ticks >= 6) {
                    MoveUtil.setSpeed(event, 0.0);
                    MoveUtil.vertical(event, 0.0);
                } else {
                    MoveUtil.setSpeed(event, this.hSpeed.getDouble());
                    if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                        MoveUtil.vertical(event, vSpeed.getDouble());
                    } else if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                        MoveUtil.vertical(event, -vSpeed.getDouble());
                    } else {
                        MoveUtil.vertical(event, 0.0);
                    }
                }
            }

            case BOOST -> {
                switch (this.stage) {
                    case 0 -> {
                        if (MoveUtil.isMoving()) {
                            if (switch (this.damage.get()) {
                                case PACKET -> PlayerUtil.selfDamage(0.0625, true, true);
                                case LOW -> PlayerUtil.selfDamageLow();
                                case JUMP_PACKET -> PlayerUtil.selfDamageJump();
                                case MINIMAL -> PlayerUtil.selfDamageMinimal();
                                case NONE -> mc.thePlayer.onGround;
                            }) {
                                this.stage = 1;
                                this.speed = this.speedSet.get();
                                if (this.pushUp.get()) {
                                    MoveUtil.vertical(event, MoveUtil.jumpHeight());
                                }
                                mc.timer.timerSpeed = this.timerStart.getFloat();
                            }
                        }
                    }

                    case 1 -> {
                        if (this.useTimer.isAny(Timer.ON_BOOST, Timer.ALWAYS)) {
                            this.timerClock.reset();
                            this.stopTimer = false;
                            mc.timer.timerSpeed = this.timerFactor.getFloat();
                        } else {
                            mc.timer.timerSpeed = 1.0F;
                        }

                        MoveUtil.setSpeed(event, speed);
                        MoveUtil.vertical(event, 0.0);
                        stage = 2;
                    }

                    case 2 -> {
                        if (this.useTimer.isAny(Timer.ON_BOOST, Timer.ALWAYS)) {
                            if (this.timerClock.hasReached(this.timerTime.getInt()) && !this.useTimer.is(Timer.ALWAYS)) {
                                if (!this.stopTimer) {
                                    this.stopTimer = true;
                                    mc.timer.timerSpeed = 1.0F;
                                }
                            } else {
                                mc.timer.timerSpeed = this.timerFactor.getFloat();
                            }
                        }

                        speed = lastDist - lastDist / MoveUtil.NCP_FRICTION;
                        speed = Math.max(speed, MoveUtil.baseSpeed());
                        MoveUtil.setSpeed(event, speed);
                        MoveUtil.vertical(event, 0.0);
                    }
                }
            }
        }
    };

    @EventLink
    public final Listener<EventPreMotion> onPre = event -> {

        switch (this.mode.get()) {
            case NCP_GLIDE -> event.onGround = true;
        }

        this.lastDist = MoveUtil.lastDistance();

        if (this.viewBobbing.getFloat() != 0.0F && MoveUtil.isMoving()) {
            mc.thePlayer.cameraYaw = 0.105F * this.viewBobbing.getFloat();
        }

    };

    @EventLink
    public final Listener<EventReceivePacket> onReceivePacket = event -> {
        switch (this.mode.get()) {
            case MINIBLOX -> {
                if (event.packet instanceof S08PacketPlayerPosLook) {
                    this.ticks = 0;
                }
            }
        }
    };

    private enum Mode {
        VANILLA,
        @DisplayName("NCP Glide") NCP_GLIDE,
        MINIBLOX,
        @Description("\"Wow arithmo i like how flokcks go ZOOM make me ZOOOM.\" - Brain dead Kid") BOOST
    }

    enum Timer {
        ON_BOOST,
        ALWAYS,
        NONE
    }


    enum Damage {
        PACKET,
        LOW,
        JUMP_PACKET,
        MINIMAL,
        NONE
    }

}
