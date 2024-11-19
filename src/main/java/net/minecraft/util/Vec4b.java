package net.minecraft.util;

public class Vec4b {
    private byte x;
    private byte y;
    private byte z;
    private byte w;

    public Vec4b(byte x, byte y, byte z, byte w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vec4b(Vec4b vec4b) {
        this.x = vec4b.x;
        this.y = vec4b.y;
        this.z = vec4b.z;
        this.w = vec4b.w;
    }

    public byte func_176110_a() {
        return this.x;
    }

    public byte func_176112_b() {
        return this.y;
    }

    public byte func_176113_c() {
        return this.z;
    }

    public byte func_176111_d() {
        return this.w;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Vec4b vec4b)) {
            return false;
        } else {
            return this.x == vec4b.x && (this.w == vec4b.w && (this.y == vec4b.y && this.z == vec4b.z));
        }
    }

    public int hashCode() {
        int i = this.x;
        i = 0x1F * i + this.y;
        i = 0x1F * i + this.z;
        i = 0x1F * i + this.w;
        return i;
    }
}
