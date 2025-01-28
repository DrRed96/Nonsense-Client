package wtf.bhopper.nonsense.util.misc;

import org.lwjgl.input.Mouse;
import wtf.bhopper.nonsense.util.minecraft.IMinecraft;

public class InputUtil implements IMinecraft {

    public static Vec2i getUnscaledMousePositions() {
        return new Vec2i(Mouse.getX(), mc.displayHeight - Mouse.getY());
    }

    public static boolean mouseIntersecting(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}
