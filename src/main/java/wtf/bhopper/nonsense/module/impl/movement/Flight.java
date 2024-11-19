package wtf.bhopper.nonsense.module.impl.movement;

import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventMove;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.MoveUtil;

import java.util.function.Supplier;

@ModuleInfo(name = "Flight",
        description = "Allows you to fly.",
        category = ModuleCategory.MOVEMENT)
public class Flight extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Flight method.", Mode.VANILLA);

    private final GroupProperty vanillaSpeed = new GroupProperty("Vanilla", "Vanilla properties.", () -> this.mode.is(Mode.VANILLA));
    private final NumberProperty hSpeed = new NumberProperty("Horizontal", "Horizontal Speed", 1.0, 0.1, 10.0, 0.01);
    private final NumberProperty vSpeed = new NumberProperty("Vertical", "Horizontal Speed", 0.5, 0.1, 10.0, 0.01);

    public Flight() {
        this.vanillaSpeed.addProperties(this.hSpeed, this.vSpeed);
        this.addProperties(this.mode, this.vanillaSpeed);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @EventLink
    public final Listener<EventMove> onMove = event -> {

        switch (this.mode.get()) {
            case VANILLA -> {
                MoveUtil.setSpeed(event, this.hSpeed.getDouble());
                if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                    MoveUtil.vertical(event, vSpeed.getDouble());
                } else if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                    MoveUtil.vertical(event, -vSpeed.getDouble());
                } else {
                    MoveUtil.vertical(event, 0.0);
                }
            }
        }

    };

    private enum Mode {
        VANILLA
    }

}
