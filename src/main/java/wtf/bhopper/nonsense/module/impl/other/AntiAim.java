package wtf.bhopper.nonsense.module.impl.other;

import io.netty.util.internal.ThreadLocalRandom;
import wtf.bhopper.nonsense.component.impl.player.RotationsComponent;
import wtf.bhopper.nonsense.event.EventPriorities;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventPreMotion;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.annotations.Description;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;

@ModuleInfo(name = "Anti Aim",
        description = "CS:GO anti aim but useless and in Minecraft",
        category = ModuleCategory.OTHER,
        searchAlias = {"Derp", "Annoy"})
public class AntiAim extends AbstractModule {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "mode", Mode.SPIN);
    private final NumberProperty pitchSet = new NumberProperty("Pitch", "Pitch", () -> this.mode.is(Mode.SPIN), 0.0F, -90.0F, 90.0F, 1.0F);
    private final NumberProperty speed = new NumberProperty("Speed", "Speed", () -> this.mode.is(Mode.SPIN), 5.0F, 1.0F, 45.0F, 1.0F);
    private final BooleanProperty lockView = new BooleanProperty("Lock View", "Gives you autism", false);

    private float spinYaw = 0.0F;

    public AntiAim() {
        super();
        this.addProperties(this.mode, this.pitchSet, this.speed, this.lockView);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @Override
    public void onEnable() {
        this.spinYaw = mc.thePlayer.rotationYaw;
    }

    @EventLink(EventPriorities.VERY_HIGH)
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

        RotationsComponent.updateServerRotations(yaw, pitch);

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
