package wtf.bhopper.nonsense.module.property.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import wtf.bhopper.nonsense.module.property.Property;
import wtf.bhopper.nonsense.util.misc.MathUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.function.Supplier;

public class NumberProperty extends Property<Double> {

    public static final NumberFormat FORMAT_INT = new DecimalFormat("#0");
    public static final NumberFormat FORMAT_DOUBLE = new DecimalFormat("#0.##");
    public static final NumberFormat FORMAT_PERCENT = new DecimalFormat("#0.##'%'");
    public static final NumberFormat FORMAT_MS = new DecimalFormat("#0'ms'");
    public static final NumberFormat FORMAT_DISTANCE = new DecimalFormat("#0.##'m'");
    public static final NumberFormat FORMAT_ANGLE = new DecimalFormat("#0.##'\u00B0'");
    public static final NumberFormat FORMAT_APS = new DecimalFormat("#0 'APS'");
    public static final NumberFormat FORMAT_PIXELS = new DecimalFormat("#0.##'px'");
    public static final NumberFormat FORMAT_TICKS = new DecimalFormat("#0 ticks");

    private final double min;
    private final double max;
    private final double increment;
    private final NumberFormat format;

    public NumberProperty(String displayName, String description, Supplier<Boolean> dependency, double value, double min, double max, double increment, NumberFormat format) {
        super(displayName, description, value, dependency);

        if (min > max) {
            throw new IllegalArgumentException("min must be smaller than max");
        }

        this.min = min;
        this.max = max;
        this.increment = increment;
        this.format = format;
    }

    public NumberProperty(String displayName, String description, Supplier<Boolean> dependency, double value, double min, double max, double increment) {
        this(displayName, description, dependency, value, min, max, increment, FORMAT_DOUBLE);
    }

    public NumberProperty(String displayName, String description, double value, double min, double max, double increment, NumberFormat format) {
        this(displayName, description, () -> true, value, min, max, increment, format);
    }

    public NumberProperty(String displayName, String description, double value, double min, double max, double increment) {
        this(displayName, description, () -> true, value, min, max, increment, FORMAT_DOUBLE);
    }

    @Override
    public void set(Double value) {
        if (value < this.min) {
            super.set(this.min);
        } else if (value > this.max) {
            super.set(this.max);
        } else {
            super.set(MathUtil.getIncremental(value, this.increment));
        }
    }

    public int getInt() {
        return this.get().intValue();
    }

    public float getFloat() {
        return this.get().floatValue();
    }

    public double getDouble() {
        return this.get();
    }

    public double getPercent() {
        return (this.get() - this.min) / (this.max - this.min);
    }

    public void setFromPercent(double percent) {
        this.set(percent * (this.max - this.min) + this.min);
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    @Override
    public String getDisplayValue() {
        return this.format.format(this.get());
    }

    @Override
    public void parseString(String str) {
        this.set(Double.parseDouble(str));
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.get());
    }

    @Override
    public void deserialize(JsonElement element) {
        try {
            this.set(element.getAsDouble());
        } catch (Exception ignored) {}
    }

}
