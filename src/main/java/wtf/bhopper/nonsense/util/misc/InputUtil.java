package wtf.bhopper.nonsense.util.misc;

import org.lwjglx.input.Mouse;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;

public class InputUtil implements MinecraftInstance {

    public static int[] getUnscaledMousePositions() {
        return new int[]{ Mouse.getX(), mc.displayHeight - Mouse.getY() };
    }

    public static boolean mouseIntersecting(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}
