package wtf.bhopper.nonsense.util.render;

public class AnimationUtil {

    public static float calculateCompensation(float target, float current, float delta, int speed) {
        float diff = current - target;

        float deltaMax = Math.max(delta, 0.02F);

        float change = (speed * deltaMax * 3.125F) < 0.25F ? 0.5F : (speed * deltaMax * 3.125F);
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
