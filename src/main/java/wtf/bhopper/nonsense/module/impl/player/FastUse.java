package wtf.bhopper.nonsense.module.impl.player;

import net.minecraft.item.EnumAction;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventPreMotion;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;

@ModuleInfo(name = "Fast Use", description = "Use items faster", category = ModuleCategory.PLAYER)
public class FastUse extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Fast use method", Mode.PACKET);
    private final NumberProperty packets = new NumberProperty("Packets", "Amount of packets to send", () -> this.mode.is(Mode.PACKET), 35, 1, 50, 1);
    private final NumberProperty timerSpeed = new NumberProperty("Speed", "Timer speed", () -> this.mode.is(Mode.TIMER), 3, 1, 10, 0.05);

    private boolean timerSetback;

    public FastUse() {
        this.autoAddProperties();
    }

    @Override
    public void onDisable() {
        if (this.timerSetback) {
            mc.timer.timerSpeed = 1.0F;
            this.timerSetback = false;
        }
    }

    @EventLink
    public final Listener<EventPreMotion> onPre = _ -> {
        if (this.isEating()) {
            switch (this.mode.get()) {
                case PACKET -> {
                    for (int i = 0; i < this.packets.getInt(); i++) {
                        PacketUtil.send(new C03PacketPlayer());
                    }
                }
                case TIMER -> {
                    mc.timer.timerSpeed = this.timerSpeed.getFloat();
                    this.timerSetback = true;
                }
            }
        } else if (this.timerSetback) {
            mc.timer.timerSpeed = 1.0F;
            this.timerSetback = false;
        }
    };

    private boolean isEating() {

        // mc.thePlayer.isEating() actually returns the Use Item entity flag so we need to make our own check

        if (!mc.thePlayer.isUsingItem()) {
            return false;
        }

        EnumAction useAction =  mc.thePlayer.getItemInUse().getItemUseAction();
        return useAction == EnumAction.EAT || useAction == EnumAction.DRINK;
    }



    private enum Mode {
        PACKET,
        TIMER
    }

}
