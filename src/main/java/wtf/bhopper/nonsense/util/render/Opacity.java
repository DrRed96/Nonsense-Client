package wtf.bhopper.nonsense.util.render;

public class Opacity {
    private float opacity;

    public Opacity(int opacity) {
        this.opacity = (float)opacity;
    }

    public void interpolate(float targetOpacity, float delta) {
        this.opacity = AnimationUtil.calculateCompensation(targetOpacity, this.opacity, delta, 20);
    }

    public void interp(float targetOpacity, int speed, float delta) {
        this.opacity = AnimationUtil.calculateCompensation(targetOpacity, this.opacity, delta, speed);
    }

    public int getOpacity() {
        return (int)this.opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public int of(int rgb) {
        return ColorUtil.alpha(rgb, this.getOpacity());
    }
}
