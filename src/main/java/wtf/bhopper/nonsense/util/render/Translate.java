package wtf.bhopper.nonsense.util.render;

public class Translate {
    private float x;
    private float y;

    public Translate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void interpolate(float targetX, float targetY, float smoothing, float delta) {
        int deltaX = (int)(Math.abs(targetX - this.x) * smoothing);
        int deltaY = (int)(Math.abs(targetY - this.y) * smoothing);
        this.x = AnimationUtil.calculateCompensation(targetX, this.x, delta, deltaX);
        this.y = AnimationUtil.calculateCompensation(targetY, this.y, delta, deltaY);
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
