package wtf.bhopper.nonsense.module.impl.combat;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.packet.EventReceivePacket;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;

@ModuleInfo(name = "Velocity",
        description = "Reduces/removes knock-back.",
        category = ModuleCategory.COMBAT,
        searchAlias = {"Anti Knock Back", "Anti KB", "Anti Velocity"})
public class Velocity extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Method for velocity.", Mode.PACKET);
    private final NumberProperty vertical = new NumberProperty("Vertical", "Vertical velocity", () -> this.mode.is(Mode.PACKET), 0.0F, 0.0F, 100.0F, 0.01F, NumberProperty.FORMAT_PERCENT);
    private final NumberProperty horizontal = new NumberProperty("Horizontal", "Horizontal velocity", () -> this.mode.is(Mode.PACKET), 0.0F, 0.0F, 100.0F, 0.01F, NumberProperty.FORMAT_PERCENT);
    private final NumberProperty airTicks = new NumberProperty("Ticks", "Air velocity ticks.", () -> this.mode.is(Mode.AIR), 5.0, 1.0, 20.0, 1.0, NumberProperty.FORMAT_INT);

    private int ticks = 0;
    private boolean cancel = false;

    public Velocity() {
        this.addProperties(this.mode, this.vertical, this.horizontal, this.airTicks);
        this.setSuffix(() -> {
            if (this.mode.is(Mode.PACKET)) {
                return vertical.getDisplayValue() + " " + horizontal.getDisplayValue();
            }
            return this.mode.getDisplayValue();
        });
    }

    @EventLink
    public final Listener<EventTick> onTick = _ -> {

        if (!PlayerUtil.canUpdate()) {
            return;
        }

        if (!mc.thePlayer.onGround) {
            this.ticks++;
        } else {
            this.ticks = 0;
        }

        if (this.ticks >= this.airTicks.get()) {
            this.cancel = true;
        } else if (ticks == 0) {
            this.cancel = false;
        }
    };

    @EventLink
    public final Listener<EventReceivePacket> onReceivePacket = event -> {

        if (event.packet instanceof S12PacketEntityVelocity packet) {

            if (packet.getEntityID() != mc.thePlayer.getEntityId()) {
                return;
            }

            switch (this.mode.get()) {
                case PACKET -> {
                    event.cancel();

                    if (this.horizontal.get() != 0.0F) {
                        double h = horizontal.get() / 100.0;
                        double mx = (double)packet.getMotionX() / 8000.0;
                        double mz = (double)packet.getMotionZ() / 8000.0;
                        mc.thePlayer.motionX = mx * h;
                        mc.thePlayer.motionZ = mz * h;
                    }

                    if (this.vertical.get() != 0.0F) {
                        double v = vertical.get() / 100.0;
                        double my = (double)packet.getMotionY() / 8000.0;
                        mc.thePlayer.motionY = my * v;
                    }

                }

                case AIR -> {
                    event.cancel();
                    if (!this.cancel) {
                        mc.thePlayer.motionY = (double)packet.getMotionY() / 8000.0;
                    }
                }
            }
        } else if (event.packet instanceof S27PacketExplosion packet) {
            switch (this.mode.get()) {
                case PACKET -> packet.setMotion(
                        packet.getMotionX() * this.horizontal.getFloat(),
                        packet.getMotionY() * this.vertical.getFloat(),
                        packet.getMotionZ() * this.horizontal.getFloat()
                );

                case AIR -> packet.setMotion(
                        packet.getMotionX() * this.horizontal.getFloat(),
                        packet.getMotionY(),
                        packet.getMotionZ() * this.horizontal.getFloat()
                );
            }
        }

    };

    enum Mode {
        PACKET,
        AIR
    }

}
