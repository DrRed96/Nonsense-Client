package wtf.bhopper.nonsense.module.impl.movement;

import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.world.EventBlockBounds;
import wtf.bhopper.nonsense.event.impl.player.movement.EventMove;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;

@ModuleInfo(name = "Phase",
        description = "Allows you to walk through blocks",
        category = ModuleCategory.MOVEMENT)
public class Phase extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Phase method", Mode.VANILLA);

    public Phase() {
        super();
        this.addProperties(this.mode);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @EventLink
    public final Listener<EventBlockBounds> onCollide = event -> {
        switch (this.mode.get()) {
            case VANILLA -> {
                mc.thePlayer.noClip = true;
                event.bounds = null;
            }
        }
    };

    @EventLink
    public final Listener<EventMove> onMove = event -> {
        switch (this.mode.get()) {
            case VANILLA -> {
                if (PlayerUtil.isInsideBlock()) {
                    MoveUtil.setSpeed(event, 1.0);
                    if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                        MoveUtil.vertical(event, 1.0);
                    } else if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                        MoveUtil.vertical(event, -1.0);
                    } else {
                        MoveUtil.vertical(event, 0.0);
                    }
                }
            }
        }
    };

    private enum Mode {
        VANILLA
    }

}
