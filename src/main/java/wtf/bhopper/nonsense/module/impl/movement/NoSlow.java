package wtf.bhopper.nonsense.module.impl.movement;

import net.minecraft.item.EnumAction;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.component.impl.packet.BlinkComponent;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.EventPriorities;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.interact.EventClickAction;
import wtf.bhopper.nonsense.event.impl.player.EventPostMotion;
import wtf.bhopper.nonsense.event.impl.player.EventPreMotion;
import wtf.bhopper.nonsense.event.impl.player.movement.EventSlowDown;
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

    private boolean ncpBlock = false;

    public NoSlow() {
        super();
        this.addProperties(this.mode, this.sprintReset, this.spoofMode, this.groundCheck);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @Override
    public void onEnable() {
        this.ncpBlock = false;
    }

    @EventLink
    public final Listener<EventSlowDown> onSlowDown = event -> {
        switch (this.mode.get()) {
            case VANILLA, NCP, SWITCH -> event.cancel();
            case LEGIT -> {
                if (mc.thePlayer.isBlocking()) {
                    event.cancel();
                }
            }
            case SPOOF -> {
                if (this.ground && mc.thePlayer.isUsingItem()) {
                    event.cancel();
                }
            }
        }

    };

    @EventLink(EventPriorities.LOW)
    public final Listener<EventClickAction> onClick = event -> {

        switch (this.mode.get()) {
            case NCP -> {
                if (event.usingItem && this.blockItem() && !event.release && (MoveUtil.isMoving() || event.left)) {
                    event.release = true;
                    this.ncpBlock = true;
                }
            }

            case LEGIT -> {
                if (this.blockItem()) {
                    if (event.usingItem) {
                        if (event.left || MoveUtil.isMoving()) {
                            event.release = true;
                        }
                        event.left = false;
                        event.right = false;
                    } else {
                        if (event.rightButton) {
                            event.right = true;
                        }
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
    public final Listener<EventPostMotion> onPost = _ -> {
        if (this.mode.is(Mode.NCP)) {
            if (this.ncpBlock) {
                PacketUtil.rightClickPackets(mc.objectMouseOver, true, true);
                this.ncpBlock = false;
            }
        }
    };

    public boolean blockItem() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItemUseAction() == EnumAction.BLOCK;
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
        LEGIT,
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
