package wtf.bhopper.nonsense.module;

import net.minecraft.util.ResourceLocation;

public enum ModuleCategory {
    COMBAT("Combat", 0xFF5555, "combat.png"),
    MOVEMENT("Movement", 0x55FF55, "movement.png"),
    PLAYER("Player", 0xAA55AA, "player.png"),
    EXPLOIT("Exploit", 0x55AAFF, "exploit.png"),
    OTHER("Other", 0xFFAA00, "other.png"),
    VISUAL("Visual", 0x0000AA, "visual.png"),
    SCRIPT("Script", 0xFFFF55, "scripts.png");

    public final String name;
    public final int color;
    public final ResourceLocation icon;

    ModuleCategory(String name, int color, String icon) {
        this.name = name;
        this.color = color | 0xFF000000;
        this.icon = new ResourceLocation("nonsense/category/" + icon);
    }
}
