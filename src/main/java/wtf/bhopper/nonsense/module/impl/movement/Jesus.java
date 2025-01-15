package wtf.bhopper.nonsense.module.impl.movement;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.world.EventBlockBounds;
import wtf.bhopper.nonsense.event.impl.player.movement.EventMove;
import wtf.bhopper.nonsense.event.impl.player.EventUpdate;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;

@ModuleInfo(name = "Jesus",
        description = "Allows you to walk on water, like Jesus!",
        category = ModuleCategory.MOVEMENT)
public class Jesus extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Method for Jesus", Mode.SOLID);
    private final EnumProperty<PushOut> pushOut = new EnumProperty<>("Push Out", "Pushes you out of water/lava", PushOut.BOOST, () -> this.mode.isAny(Mode.SOLID, Mode.NCP));

    public Jesus() {
        super();
        this.addProperties(this.mode, this.pushOut);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @EventLink
    public final Listener<EventUpdate> onUpdate = _ -> {
        if (this.mode.is(Mode.DOLPHIN)) {
            mc.theWorld.handleMaterialAcceleration(mc.thePlayer.getEntityBoundingBox().expand(0.0, -0.1, 0.0), Material.water, mc.thePlayer);
        }
    };

    @EventLink
    public final Listener<EventBlockBounds> onCollide = event -> {
        if (!PlayerUtil.canUpdate()) {
            return;
        }

         switch (this.mode.get()) {
             case SOLID, NCP -> {
                 if (event.block instanceof BlockLiquid && this.shouldSetBounds()) {
                     event.bounds = new AxisAlignedBB(event.pos.getX(), event.pos.getY(), event.pos.getZ(), event.pos.getX() + 1, event.pos.getY() + 1, event.pos.getZ() + 1);
                 }
             }
         }
    };

    @EventLink
    public final Listener<EventMove> onMove = event -> {
        switch (this.mode.get()) {
            case SOLID -> {
                if (PlayerUtil.isInLiquid() && !mc.thePlayer.movementInput.sneak) {
                    if (!this.pushOut.is(PushOut.NONE)) {
                        MoveUtil.vertical(event, this.canBoost() ? 0.3 : 0.06);
                    }
                }
            }
            case NCP -> {
                if (PlayerUtil.isInLiquid() && !mc.thePlayer.movementInput.sneak) {
                    if (!this.pushOut.is(PushOut.NONE)) {
                        MoveUtil.vertical(event, this.canBoost() ? 0.3 : 0.06);
                    }
                } else if (PlayerUtil.isOnLiquid() && mc.thePlayer.ticksExisted % 2 == 0) {
                    event.y -= 0.015625;
                }
            }
        }
    };

    private boolean shouldSetBounds() {
        try {
            return !PlayerUtil.isInLiquid() && !mc.thePlayer.movementInput.sneak;
        } catch (NullPointerException ignored) {
            return false;
        }
    }

    private boolean canBoost() {
        if (!mc.thePlayer.isOffsetPositionInLiquid(mc.thePlayer.motionX, 0.66, mc.thePlayer.motionZ)) {
            return false;
        }

        return this.pushOut.is(PushOut.BOOST) || mc.thePlayer.isCollidedHorizontally;
    }

    private enum Mode {
        SOLID,
        @DisplayName("NCP") NCP,
        DOLPHIN
    }

    private enum PushOut {
        BOOST,
        LEGIT,
        NONE
    }

}
