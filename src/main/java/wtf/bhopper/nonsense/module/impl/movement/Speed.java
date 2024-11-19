package wtf.bhopper.nonsense.module.impl.movement;

import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventMove;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.MoveUtil;

import java.util.function.Supplier;

@ModuleInfo(name = "Speed",
        description = "Increases your move speed.",
        category = ModuleCategory.MOVEMENT)
public class Speed extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Method for speed.", Mode.VANILLA);
    private final NumberProperty speedSet = new NumberProperty("Speed", "Move speed.", () -> this.mode.is(Mode.VANILLA), 1.0, 0.5, 3.0, 0.01);

    public Speed() {
        this.addProperties(this.mode, this.speedSet);
        this.setSuffix(mode::getDisplayValue);
    }

    @EventLink
    public final Listener<EventMove> onMove = event -> {

        switch (this.mode.get()) {
            case VANILLA -> {
                if (MoveUtil.isMoving()) {
                    MoveUtil.setSpeed(event, this.speedSet.getDouble());
                }
            }
        }

    };

    private enum Mode {
        VANILLA
    }

}
