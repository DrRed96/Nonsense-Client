package wtf.bhopper.nonsense.module.impl.other;

import io.netty.util.internal.ThreadLocalRandom;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.annotations.Description;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;

import java.util.function.Supplier;

@ModuleInfo(name = "Anti Aim", description = "CS:GO anti aim but useless and in Minecraft", category = ModuleCategory.OTHER)
public class AntiAim extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "mode", Mode.SPIN);
    private final NumberProperty pitchSet = new NumberProperty("Pitch", "Pitch", () -> this.mode.is(Mode.SPIN), 0.0F, -90.0F, 90.0F, 1.0F);
    private final NumberProperty speed = new NumberProperty("Speed", "Speed", () -> this.mode.is(Mode.SPIN), 5.0F, 1.0F, 45.0F, 1.0F);
    private final BooleanProperty lockView = new BooleanProperty("Lock View", "Gives you autism", false);

    private float spinYaw = 0.0F;

    public AntiAim() {
        this.addProperties(this.mode, this.pitchSet, this.speed, this.lockView);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @Override
    public void onEnable() {
        this.spinYaw = mc.thePlayer.rotationYaw;
    }

    @EventLink
    public final Listener<EventPreMotion> onPre = event -> {
        float yaw = event.yaw;
        float pitch = event.pitch;

        switch (this.mode.get()) {
            case SPIN -> {
                yaw = spinYaw += this.speed.get();
                pitch = this.pitchSet.getFloat();
            }
            case CRAZY -> {
                yaw = ThreadLocalRandom.current().nextInt(-180, 180);
                pitch = ThreadLocalRandom.current().nextInt(-90, 90);
            }
            case BACKWARDS -> {
                yaw += 180.0F;
                pitch = -pitch;
            }
            case DEATH -> pitch = 180.0F;
        }

        event.yaw = yaw;
        event.pitch = pitch;

        if (lockView.get()) {
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
        }
    };

    public enum Mode {
        SPIN,
        CRAZY,
        BACKWARDS,
        @Description("Will likely ban on most anticheats") DEATH
    }

}
