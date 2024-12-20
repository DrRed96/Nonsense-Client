package wtf.bhopper.nonsense.module.impl.movement;

import net.minecraft.item.EnumAction;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventClickAction;
import wtf.bhopper.nonsense.event.impl.player.EventPostMotion;
import wtf.bhopper.nonsense.event.impl.player.EventPreMotion;
import wtf.bhopper.nonsense.event.impl.player.EventSlowDown;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.impl.combat.AutoBlock;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;

@ModuleInfo(name = "No Slow",
        description = "Prevents you from being slowed down while using an item.",
        category = ModuleCategory.MOVEMENT,
        searchAlias = "No Slow Down")
public class NoSlow extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Method for no slow.", Mode.VANILLA);
    private final BooleanProperty sprintReset = new BooleanProperty("Sprint Reset", "Prevents sprint from being reset.", true);
    private final EnumProperty<SpoofMode> spoofMode = new EnumProperty<>("Spoof Mode", "Method for spoofing", SpoofMode.GROUND, () -> this.mode.is(Mode.SPOOF));
    private final EnumProperty<GroundCheck> groundCheck = new EnumProperty<>("Ground Check", "Checks if you were off ground before spoofing", GroundCheck.AUTO_JUMP, () -> this.mode.is(Mode.SPOOF));

    private boolean ground = false;

    public NoSlow() {
        this.addProperties(this.mode, this.sprintReset, this.spoofMode, this.groundCheck);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @EventLink
    public final Listener<EventSlowDown> onSlowDown = event -> {
        if (!Nonsense.module(AutoBlock.class).canBlock()) {
            switch (this.mode.get()) {
                case VANILLA, NCP, SWITCH -> event.cancel();
                case SPOOF -> {
                    if (this.ground && mc.thePlayer.isUsingItem()) {
                        event.cancel();
                    }
                }
            }
        }
    };

    @EventLink
    public final Listener<EventClickAction> onClick = event -> {

        if (!Nonsense.module(AutoBlock.class).canBlock()) {
            switch (this.mode.get()) {
                case NCP -> {
                    if (this.isBlocking() && MoveUtil.isMoving()) {
                        PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    }
                }
            }
        }
    };

    @EventLink
    public final Listener<EventPreMotion> onPre = event -> {
        if (!Nonsense.module(AutoBlock.class).canBlock()) {
            switch (this.mode.get()) {
                case SWITCH -> {
                    if (mc.thePlayer.isUsingItem()) {
                        PacketUtil.send(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                        PacketUtil.send(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    }
                }
                case SPOOF -> {

                    switch (this.groundCheck.get()) {
                        case ENABLED -> {
                            if (mc.thePlayer.isUsingItem()) {
                                if (!this.ground && !mc.thePlayer.onGround) {
                                    this.ground = true;
                                }
                            } else {
                                this.ground = false;
                            }
                        }
                        case AUTO_JUMP -> {
                            if (mc.thePlayer.isUsingItem()) {
                                if (!this.ground && !mc.thePlayer.onGround) {
                                    this.ground = true;
                                } else if (mc.thePlayer.onGround && !this.ground) {
                                    mc.thePlayer.jump();
                                }
                            } else {
                                this.ground = false;
                            }
                        }
                        case NONE -> this.ground = true;
                    }

                    if (mc.thePlayer.isUsingItem()) {
                        if (this.ground && mc.thePlayer.onGround) {
                            switch (this.spoofMode.get()) {
                                case GROUND -> event.onGround = false;
                                case OFFSET -> event.y += 1.0E-14;
                            }
                        }
                    }
                }
            }
        }
    };

    @EventLink
    public final Listener<EventPostMotion> onPost = event -> {

        if (!Nonsense.module(AutoBlock.class).canBlock()) {
            switch (this.mode.get()) {
                case NCP -> {
                    if (this.isBlocking() && MoveUtil.isMoving()) {
                        PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    }
                }
            }
        }
    };

    public boolean isBlocking() {
        return mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItemUseAction() == EnumAction.BLOCK;
    }

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
        @DisplayName("NCP") NCP,
        SWITCH,
        SPOOF
    }

    private enum SpoofMode {
        GROUND,
        OFFSET
    }

    private enum GroundCheck {
        ENABLED,
        AUTO_JUMP,
        NONE
    }

}
