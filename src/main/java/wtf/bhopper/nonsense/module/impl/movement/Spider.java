package wtf.bhopper.nonsense.module.impl.movement;

import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventMove;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;

@ModuleInfo(name = "Spider",
        description = "Allows you to climb up walls",
        category = ModuleCategory.MOVEMENT,
        searchAlias = "Wall Climb")
public class Spider extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Method for spider.", Mode.VANILLA);

    public Spider() {
        this.addProperties(this.mode);
    }

    @EventLink
    public final Listener<EventMove> onMove = event -> {
        switch (this.mode.get()) {
            case VANILLA -> {
                if (mc.thePlayer.isCollidedHorizontally) {
                    MoveUtil.vertical(event, 0.42);
                }
            }
        }
    };

    @Override
    public String getSuffix() {
        return this.mode.getDisplayValue();
    }

    private enum Mode {
        VANILLA
    }

}
