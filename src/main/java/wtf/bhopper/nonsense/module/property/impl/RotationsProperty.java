package wtf.bhopper.nonsense.module.property.impl;

import wtf.bhopper.nonsense.module.property.IPropertyContainer;
import wtf.bhopper.nonsense.util.minecraft.player.Rotation;
import wtf.bhopper.nonsense.util.minecraft.player.RotationUtil;

public class RotationsProperty extends GroupProperty {

    public final EnumProperty<RotationMode> mode = new EnumProperty<>("Mode", "Rotations method", RotationMode.INSTANT);
    public final NumberProperty linear = new NumberProperty("Linear Amount", "Amount to change by with linear rotations.", () -> this.mode.is(RotationMode.LINEAR), 80.0, 1.0, 100.0, 1.0, NumberProperty.FORMAT_PERCENT);
    public final NumberProperty maxAngle = new NumberProperty("Max Angle", "Maximum angle distance.", () -> this.mode.is(RotationMode.STEP), 90.0, 10.0, 180.0, 1.0, NumberProperty.FORMAT_ANGLE);
    public final NumberProperty stepAngle = new NumberProperty("Step Angle", "Amount to rotate if your angle distance exceeds the max angle.", () -> this.mode.is(RotationMode.STEP), 90.0, 10.0, 180.0, 1.0, NumberProperty.FORMAT_ANGLE);

    public RotationsProperty(String displayName, String description, IPropertyContainer owner) {
        super(displayName, description, owner);
        this.addProperties(this.mode, this.linear, this.maxAngle, this.stepAngle);
    }

    public Rotation rotate(Rotation start, Rotation end) {
        return switch (this.mode.get()) {
            case INSTANT -> end;
            case LINEAR -> RotationUtil.lerp(start, end, linear.getFloat() / 100.0F);
            case STEP -> RotationUtil.step(start, end, maxAngle.getFloat(), stepAngle.getFloat());
        };
    }

    public enum RotationMode {
        INSTANT,
        LINEAR,
        STEP
    }

}
