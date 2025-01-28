package wtf.bhopper.nonsense.util.misc;

import org.lwjgl.util.vector.Vector2f;

public class Vec2i {

    public final int x;
    public final int y;

    public Vec2i() {
        this.x = 0;
        this.y = 0;
    }

    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2i(Vector2f other) {
        this.x = (int)other.getX();
        this.y = (int)other.getY();
    }

    public Vec2i add(int x, int y) {
        return new Vec2i(this.x + x, this.y + y);
    }

    public Vec2i add(Vec2i other) {
        return this.add(other.x, other.y);
    }

}
