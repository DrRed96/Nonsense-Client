package wtf.bhopper.nonsense.module.impl.movement;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.bhopper.nonsense.event.impl.EventClickAction;
import wtf.bhopper.nonsense.event.impl.EventPostMotion;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
import wtf.bhopper.nonsense.event.impl.EventSlowDown;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.util.DisplayName;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;

public class NoSlow extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "Mode", Mode.VANILLA);

    public NoSlow() {
        super("No Slow", "Prevents you from being slowed while using items", Category.MOVEMENT);
        this.addSettings(this.mode);
    }

    @EventHandler
    public void onSlow(EventSlowDown event) {
        switch (mode.get()) {
            case VANILLA:
            case NCP:
                event.cancel();
                break;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClick(EventClickAction event) {
        if (this.mode.is(Mode.NCP)) {
            if (mc.thePlayer.isBlocking() && event.click) {
                PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
        }
    }

    @EventHandler
    public void onPostMotion(EventPostMotion event) {
        if (this.mode.is(Mode.NCP)) {
            if (mc.thePlayer.isBlocking()) {
                PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            }
        }
    }

    @Override
    public String getSuffix() {
        return mode.getDisplayValue();
    }

    public boolean usingItemFixed() {
        if (this.isEnabled()) {
            return false;
        }

        return mc.thePlayer.isUsingItem();
    }

    public enum Mode {
        VANILLA,
        @DisplayName("NCP") NCP
    }

}
