package wtf.bhopper.nonsense.module.impl.movement;

import net.minecraft.item.EnumAction;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.Cancellable;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.*;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.impl.combat.AutoBlock;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.minecraft.PacketUtil;
import wtf.bhopper.nonsense.util.minecraft.PlayerUtil;

@ModuleInfo(name = "No Slow",
        description = "Prevents you from being slowed down while using an item.",
        category = ModuleCategory.MOVEMENT)
public class NoSlow extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Method for no slow.", Mode.VANILLA);
    private final BooleanProperty sprintReset = new BooleanProperty("Sprint Reset", "Prevents sprint from being reset.", true);

    public NoSlow() {
        this.addProperties(this.mode, this.sprintReset);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @EventLink
    public final Listener<EventSlowDown> onSlowDown = Cancellable::cancel;

    @EventLink
    public final Listener<EventClickAction> onPre = eventPreMotion -> {
        if (this.mode.is(Mode.NCP)) {
            if (mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItemUseAction() == EnumAction.BLOCK && !Nonsense.module(AutoBlock.class).canBlock()) {
                PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
        }
    };

    @EventLink
    public final Listener<EventPostMotion> onPost = eventPreMotion -> {
        if (this.mode.is(Mode.NCP)) {
            if (mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItemUseAction() == EnumAction.BLOCK && !Nonsense.module(AutoBlock.class).canBlock()) {
                PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            }
        }
    };

    public boolean usingItemFixed() {
        if (this.canSprint()) {
            return false;
        }

        return mc.thePlayer.isUsingItem();
    }

    public boolean canSprint() {
        return this.isToggled() && this.sprintReset.get();
    }

    private enum Mode {
        VANILLA,
        @DisplayName("NCP") NCP
    }

}
