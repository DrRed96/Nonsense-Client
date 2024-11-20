package wtf.bhopper.nonsense.module.impl.player;

import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.PlayerUtil;

@ModuleInfo(name = "Game Speed", description = "Changes the game speed", category = ModuleCategory.PLAYER)
public class GameSpeed extends Module {

    private final NumberProperty speed = new NumberProperty("Speed", "Game speed multiplier", 1.5, 0.1, 10.0, 0.01);

    public GameSpeed() {
        this.addProperties(this.speed);
        this.setSuffix(this.speed::getDisplayValue);
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
    }

    @EventLink
    public final Listener<EventTick> onTick = event -> {
        if (PlayerUtil.canUpdate()) {
            mc.timer.timerSpeed = this.speed.getFloat();
        } else {
            this.toggle(false);
        }
    };

}
