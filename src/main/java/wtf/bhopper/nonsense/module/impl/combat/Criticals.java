package wtf.bhopper.nonsense.module.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MovingObjectPosition;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventPreClick;
import wtf.bhopper.nonsense.event.impl.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.PacketUtil;
import wtf.bhopper.nonsense.util.minecraft.PlayerUtil;

@ModuleInfo(name = "Criticals",
        description = "Makes you do critical hits.",
        category = ModuleCategory.COMBAT)
public class Criticals extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Criticals method.", Mode.PACKET);
    private final NumberProperty delay = new NumberProperty("Delay", "Delay between critical hits in ticks.", 15, 0, 20, 1);
    private final BooleanProperty safe = new BooleanProperty("Safe", "Prevents you from doing a critical hit if the target is on damage cool-down.", false);

    private int ticks = 0;

    public Criticals() {
        this.addProperties(this.mode, this.delay, this.safe);
        this.setSuffix(() -> this.mode.getDisplayValue() + " " + (this.safe.get() ? "Safe" : delay.getDisplayValue()));
    }

    @EventLink
    public final Listener<EventTick> onTick = event -> {
        if (PlayerUtil.canUpdate()) {
            this.ticks--;
        } else {
            this.ticks = 0;
        }
    };

    @EventLink
    public final Listener<EventPreClick> onClick = event -> {

        if (event.mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mc.leftClickCounter <= 0) {
            Entity entity = event.mouseOver.entityHit;

            if (!(entity instanceof EntityLivingBase)) {
                return;
            }

            if (this.ticks > 0) {
                return;
            }

            if (this.safe.get() && entity.hurtResistantTime > 0) {
                return;
            }

            switch (this.mode.get()) {
                case PACKET -> {
                    PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
                    PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.001, mc.thePlayer.posZ, false));
                }

                case PACKET_LOW -> {
                    PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-14, mc.thePlayer.posZ, false));
                    PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                }

            }

        }

    };

    private enum Mode {
        PACKET,
        PACKET_LOW
    }

}
