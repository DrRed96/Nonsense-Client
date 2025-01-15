package wtf.bhopper.nonsense.module.impl.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventPreMotion;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.world.BlockUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;

@ModuleInfo(
        name = "Anti Fall",
        description = "Prevents you from falling off edges.",
        category = ModuleCategory.MOVEMENT,
        searchAlias = {"Anti Void", "No Void"})
public class AntiFall extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Anti Fall method", Mode.PACKET);
    private final NumberProperty height = new NumberProperty("Height", "Fall height", 2.0F, 1.0F, 10.0F, 0.5F);
    private final BooleanProperty voidOnly = new BooleanProperty("Void Only", "Only prevents you from falling if you're going to fall into the void", true);
    private final BooleanProperty scaffold = new BooleanProperty("Scaffold", "Enables scaffold upon saving", false);

    private BlockPos lastGroundPos = null;

    public AntiFall() {
        super();
        this.addProperties(this.mode, this.height, this.voidOnly, this.scaffold);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @EventLink
    public final Listener<EventPreMotion> onPre = event -> {
        if (BlockUtil.isSolid(mc.thePlayer.getPosition().down())) {
            lastGroundPos = mc.thePlayer.getPosition();
        }

        if (this.shouldSave()) {
            switch (mode.get()) {
                case SET_BACK -> {
                    if (this.lastGroundPos != null) {
                        mc.thePlayer.setPosition(lastGroundPos.getX() + 0.5, lastGroundPos.getY(), lastGroundPos.getZ() + 0.5);
                        mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
                        lastGroundPos = null;
                    }
                }

                // Will cause a setback on certain anti-cheats (such as NCP) which prevents you from falling into the void
                case PACKET -> PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition());

                case BOUNCE -> {
                    if (!mc.thePlayer.isSneaking()) {
                        mc.thePlayer.motionY = 1.0;
                        mc.thePlayer.fallDistance = 0.0F;
                        event.onGround = true;
                    }
                }
            }

            if (this.scaffold.get()) {
                Nonsense.module(Scaffold.class).toggle(true);
            }

        }
    };

    private boolean shouldSave() {
        return mc.thePlayer.fallDistance > this.height.get() &&
                (!voidOnly.get() || !PlayerUtil.isBlockUnder() &&
                        !mc.thePlayer.capabilities.isFlying &&
                        !Nonsense.module(Flight.class).isToggled());
    }

    private enum Mode {
        SET_BACK,
        PACKET,
        BOUNCE
    }

}
