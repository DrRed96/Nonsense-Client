package net.minecraft.world.border;

public enum EnumBorderStatus
{
    GROWING(0x40ff80),
    SHRINKING(0xff3030),
    STATIONARY(0x20a0ff);

    private final int id;

    EnumBorderStatus(int id)
    {
        this.id = id;
    }

    /**
     * Returns an integer that represents the state of the world border. Growing, Shrinking and Stationary all have
     * unique values.
     */
    public int getID() {
        return this.id;
    }
}
