package wtf.bhopper.nonsense.module.impl.player;

import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;

@ModuleInfo(name = "Game Speed",
        description = "Changes the game speed",
        category = ModuleCategory.PLAYER,
        searchAlias = "Timer")
public class GameSpeed extends Module {

    private final NumberProperty speed = new NumberProperty("Speed", "Game speed multiplier", 1.5, 0.1, 10.0, 0.01);

    public GameSpeed() {
        super();
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
