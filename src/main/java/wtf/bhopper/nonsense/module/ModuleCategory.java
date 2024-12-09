package wtf.bhopper.nonsense.module;

public enum ModuleCategory {
    COMBAT("Combat", 0xFF5555),
    MOVEMENT("Movement", 0x55FF55),
    PLAYER("Player", 0xAA55AA),
    EXPLOIT("Exploit", 0x55AAFF),
    OTHER("Other", 0xFFAA00),
    VISUAL("Visual", 0x0000AA),
    SCRIPT("Script", 0xFFFF55);

    public final String name;
    public final int color;

    ModuleCategory(String name, int color) {
        this.name = name;
        this.color = color | 0xFF000000;
    }
}
