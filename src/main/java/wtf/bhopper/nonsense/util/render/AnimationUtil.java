package wtf.bhopper.nonsense.util.render;

public class AnimationUtil {

    public static float calculateCompensation(float target, float current, float delta, int speed) {
        float diff = current - target;
        if (delta < 0.02F) {
            delta = 0.02F;
        }

        float change = (speed * delta * 3.125F) < 0.25F ? 0.5F : (speed * delta * 3.125F);
        if (diff > (float)speed) {
            current = current - change;
            if (current < target) {
                current = target;
            }
        } else if (diff < (float)(-speed)) {
            current = current + change;
            if (current > target) {
                current = target;
            }
        } else {
            current = target;
        }

        return current;
    }

}
