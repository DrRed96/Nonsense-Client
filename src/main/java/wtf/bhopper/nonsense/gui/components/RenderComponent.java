package wtf.bhopper.nonsense.gui.components;

import org.lwjglx.util.vector.Vector2f;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.render.NVGHelper;

public abstract class RenderComponent implements MinecraftInstance {

    private final String name;
    private final NumberProperty x;
    private final NumberProperty y;

    private boolean enabled;
    private int width;
    private int height;

    public RenderComponent(String name) {
        this(name, 0, 0, 0, 0);
    }

    public RenderComponent(String name, int x, int y, int width, int height) {
        this.name = name;
        this.x = new NumberProperty(name + " X Position", "X", () -> false, x, 0.0, Double.MAX_VALUE, 1.0, NumberProperty.FORMAT_PIXELS);
        this.y = new NumberProperty(name + " Y Position", "Y", () -> false, y, 0.0, Double.MAX_VALUE, 1.0, NumberProperty.FORMAT_PIXELS);
        this.width = width;
        this.height = height;
        Hud.addComponent(this);
    }

    public abstract void draw(float delta, int mouseX, int mouseY, boolean bypass);

    public void onClick(int x, int y, int button) {}

    private void nvgTranslate() {
        NVGHelper.translate(this.getX(), this.getY());
    }

    public String getName() {
        return this.name;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getX() {
        return this.x.getInt();
    }

    public int getY() {
        return this.y.getInt();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setX(int x) {
        this.x.set((double)x);
    }

    public void setY(int y) {
        this.y.set((double)y);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean mouseIntersecting(int mouseX, int mouseY) {
        int x = this.getX();
        int y = this.getY();
        return mouseX >= x && mouseX <= x + this.width && mouseY >= y && mouseY <= y + this.height;
    }

    public void drawOutline() {
        int x = this.getX();
        int y = this.getY();

        NVGHelper.begin();
        NVGHelper.drawLine(1.5F, 0xFFAAAAAA,
                new Vector2f(x, y),
                new Vector2f(x + width, y),
                new Vector2f(x + width, y + height),
                new Vector2f(x, y + height),
                new Vector2f(x, y)
        );
        NVGHelper.end();

    }

    public NumberProperty[] getSettings() {
        return new NumberProperty[]{ this.x, this.y };
    }

}
