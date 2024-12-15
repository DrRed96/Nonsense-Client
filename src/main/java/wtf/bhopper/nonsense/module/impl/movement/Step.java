package wtf.bhopper.nonsense.module.impl.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventPostStep;
import wtf.bhopper.nonsense.event.impl.EventPreStep;
import wtf.bhopper.nonsense.event.impl.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.MoveUtil;
import wtf.bhopper.nonsense.util.minecraft.PacketUtil;

@ModuleInfo(name = "Step",
        description = "Allows you to step up blocks",
        category = ModuleCategory.MOVEMENT)
public class Step extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Step method", Mode.VANILLA);
    private final NumberProperty height = new NumberProperty("Height", "Step height", () -> this.mode.is(Mode.VANILLA), 1.0, 1.0, 10.0, 0.5);

    private final GroupProperty timerGroup = new GroupProperty("Timer", "Uses timer to help bypass");
    private final BooleanProperty timerEnable = new BooleanProperty("Enable", "Enable timer", false);
    private final NumberProperty timerSpeed = new NumberProperty("Speed", "Timer speed", 0.55F, 0.1F, 1.0F, 0.05F);
    private final BooleanProperty speedDisable = new BooleanProperty("Speed Disable", "Disables step when using speed.", true);

    private final NumberProperty packets = new NumberProperty("Offsets", "Number of offsets to send.", () -> this.mode.is(Mode.MOTION), 3, 2, 11, 1);

    private boolean resetTimer = false;

    public Step() {
        this.timerGroup.addProperties(this.timerEnable, this.timerSpeed);
        this.addProperties(this.mode, this.height, this.timerGroup, this.speedDisable, this.packets);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @EventLink
    public final Listener<EventTick> onTick = event -> {
        if (this.resetTimer) {
            mc.timer.timerSpeed = 1.0F;
            this.resetTimer = false;
        }
    };

    @EventLink
    public final Listener<EventPreStep> onPreStep = event -> {
        if (!mc.thePlayer.movementInput.jump && mc.thePlayer.isCollidedVertically && (!this.speedDisable.get() || !Nonsense.module(Speed.class).isToggled())) {
            event.height = mode.isAny(Mode.NCP, Mode.MOTION) ? 1.0 : this.height.get();
        }
    };

    @EventLink
    public final Listener<EventPostStep> onPostStep = event -> {
        if (event.realHeight >= 0.68) {
            switch (this.mode.get()) {
                case NCP -> {
                    for (int i = 1; i <= 2; i++) {
                        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + MoveUtil.getPosYForJumpTick(i), mc.thePlayer.posZ, mc.thePlayer.onGround));
                    }
                }
                case MOTION -> {
                    for (int i = 1; i <= packets.getInt(); i++) {
                        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + MoveUtil.getPosYForJumpTick(i), mc.thePlayer.posZ, mc.thePlayer.onGround));
                    }
                }
            }
        }

        if (event.realHeight > 0.6 && this.timerEnable.get()) {
            mc.timer.timerSpeed = this.timerSpeed.getFloat();
            this.resetTimer = true;
        }
    };

    private enum Mode {
        VANILLA,
        @DisplayName("NCP") NCP,
        MOTION
    }

}
