package net.minecraft.util;

public class MouseFilter
{
    private float field_76336_a;
    private float field_76334_b;
    private float field_76335_c;

    /**
     * Smooths mouse input
     */
    public float smooth(float value, float delta)
    {
        this.field_76336_a += value;
        value = (this.field_76336_a - this.field_76334_b) * delta;
        this.field_76335_c += (value - this.field_76335_c) * 0.5F;

        if (value > 0.0F && value > this.field_76335_c || value < 0.0F && value < this.field_76335_c)
        {
            value = this.field_76335_c;
        }

        this.field_76334_b += value;
        return value;
    }

    public void reset()
    {
        this.field_76336_a = 0.0F;
        this.field_76334_b = 0.0F;
        this.field_76335_c = 0.0F;
    }
}
