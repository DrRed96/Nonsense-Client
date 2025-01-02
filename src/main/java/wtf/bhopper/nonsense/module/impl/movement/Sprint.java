package wtf.bhopper.nonsense.module.impl.movement;

import net.minecraft.potion.Potion;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.packet.EventActionStates;
import wtf.bhopper.nonsense.event.impl.player.EventPostMotion;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;

@ModuleInfo(name = "Sprint",
        description = "Makes you sprint.",
        category = ModuleCategory.MOVEMENT)
public class Sprint extends Module {

    private final BooleanProperty omniSprint = new BooleanProperty("Omni Sprint", "Allows you to sprint in all directions", false);
    private final BooleanProperty keepSprint = new BooleanProperty("Keep Sprint", "Prevents sprint from being reset while attacking", false);
    private final EnumProperty<ServerState> serverState = new EnumProperty<>("Server State", "Server sprinting state", ServerState.DEFAULT);

    public Sprint() {
        this.addProperties(this.omniSprint, this.keepSprint, this.serverState);
    }

    @EventLink
    public final Listener<EventPostMotion> onPost = _ -> mc.thePlayer.setSprinting(MoveUtil.canSprint(this.omniSprint.get()));

    @EventLink
    public final Listener<EventActionStates> onActionStates = event -> {
        if (event.sprinting) {
            event.sprinting = switch (this.serverState.get()) {
                case DEFAULT -> event.sprinting;
                case BLOCKED -> false;
                case VALID_ONLY -> mc.thePlayer.moveForward > 0.8F &&
                        !mc.thePlayer.isSneaking() &&
                        (mc.thePlayer.getFoodStats().getFoodLevel() >= 6 || mc.thePlayer.capabilities.allowFlying) &&
                        !mc.thePlayer.isCollidedHorizontally &&
                        !mc.thePlayer.isPotionActive(Potion.moveSlowdown) &&
                        !mc.thePlayer.isUsingItem();
            };
        }
    };

    public boolean omni() {
        return this.isToggled() && this.omniSprint.get();
    }

    public boolean keepSprint() {
        return this.isToggled() && this.keepSprint.get();
    }

    private enum ServerState {
        DEFAULT,
        BLOCKED,
        VALID_ONLY
    }

}
