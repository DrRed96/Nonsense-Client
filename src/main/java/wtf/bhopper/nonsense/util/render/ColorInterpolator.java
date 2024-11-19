package wtf.bhopper.nonsense.util.render;

import java.awt.*;

public class ColorInterpolator {

    private Color target;
    private Color color;

    public ColorInterpolator(Color target) {
        this.target = this.color = target;
    }

    public ColorInterpolator(int target) {
        this.target = this.color = new Color(target, true);
    }

    public void setTarget(Color target) {
        this.target = target;
    }

    public void setTarget(int target) {
        this.target = new Color(target, true);
    }

    public Color get() {
        return this.color;
    }

    public int getRGB() {
        return this.color.getRGB();
    }

    public void interpolate(int speed, float delta) {
        this.color = ColorUtil.interpolate(this.color, this.target, speed, delta);
    }

}
