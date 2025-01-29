package wtf.bhopper.nonsense.module.impl.player;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventPreMotion;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;

@ModuleInfo(name = "No Fall",
        category = ModuleCategory.PLAYER,
        description = "Prevents fall damage.",
        searchAlias = "No Fall Damage")
public class NoFall extends AbstractModule {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Method to prevent fall damage.", Mode.SPOOF);
    private final BooleanProperty timer = new BooleanProperty("Timer", "Uses timer to help bypass.", false, () -> this.mode.is(Mode.PACKET));

    private float lastFallDistance = 0.0F;
    private boolean timerSetBack = false;

    public NoFall() {
        super();
        this.addProperties(this.mode, this.timer);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @Override
    public void onDisable() {
        if (this.timerSetBack) {
            mc.timer.timerSpeed = 1.0F;
            this.timerSetBack = false;
        }
    }

    @EventLink
    public final Listener<EventPreMotion> onPreMotion = event -> {

        if (this.timerSetBack) {
            mc.timer.timerSpeed = 1.0F;
            this.timerSetBack = false;
        }

        if (mc.thePlayer.onGround) {
            this.lastFallDistance = 0.0F;
        }

        switch (this.mode.get()) {
            case SPOOF -> {
                if (this.willTakeDamage()) {
                    event.onGround = true;
                    this.lastFallDistance = mc.thePlayer.fallDistance;
                }
            }

            case PACKET -> {
                if (this.willTakeDamage()) {
                    PacketUtil.send(new C03PacketPlayer(true));
                    this.lastFallDistance = mc.thePlayer.fallDistance;
                    if (this.timer.get()) {
                        mc.timer.timerSpeed = 0.5F;
                        this.timerSetBack = true;
                    }
                }
            }

            case NO_GROUND -> event.onGround = false;

            case VERUS -> {
                if (this.willTakeDamage()) {
                    mc.thePlayer.motionY = 0.0;
                    mc.thePlayer.motionX *= 0.6;
                    mc.thePlayer.motionZ *= 0.6;
                    event.onGround = true;
                    this.lastFallDistance = mc.thePlayer.fallDistance;
                }
            }

            case WATCHDOG -> {
                if (this.willTakeDamage()) {
                    event.y += 1.0E-13;
                    mc.timer.timerSpeed = 0.5F;
                    PacketUtil.send(new C08PacketPlayerBlockPlacement(BlockPos.ORIGIN, 0, new ItemStack(Items.water_bucket, 1), 0.5F, 0.5F, 0.5F));
                    PacketUtil.send(new C03PacketPlayer(true));
                    this.lastFallDistance = mc.thePlayer.fallDistance;
                    this.timerSetBack = true;
                }
            }

            case MINIBLOX -> {
                try {
                    if (this.willTakeDamage()) {
                        Vec3 start = mc.thePlayer.getPositionEyes(1.0F);
                        Vec3 end = start.subtract(0.0, start.yCoord, 0.0);
                        MovingObjectPosition result = mc.theWorld.rayTraceBlocks(start, end, false, false, false);
                        if (result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, result.hitVec.yCoord, mc.thePlayer.posZ, true));
                            this.lastFallDistance = mc.thePlayer.fallDistance;
                        }
                    }
                } catch (NullPointerException _) {}
            }
        }
    };

    private boolean willTakeDamage() {
        return (mc.thePlayer.fallDistance - this.lastFallDistance) - mc.thePlayer.motionY > 3.0;
    }

    private enum Mode {
        SPOOF,
        PACKET,
        NO_GROUND,
        VERUS,
        WATCHDOG,
        MINIBLOX
    }

}
