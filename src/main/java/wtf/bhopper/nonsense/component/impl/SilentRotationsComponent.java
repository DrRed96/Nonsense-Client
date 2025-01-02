package wtf.bhopper.nonsense.component.impl;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.component.Component;
import wtf.bhopper.nonsense.util.misc.MathUtil;

public class SilentRotationsComponent extends Component {

    public float serverYaw = 0.0F;
    public float serverPitch = 0.0F;
    public float prevServerYaw = 0.0F;
    public float prevServerPitch = 0.0F;

    public static void updateServerRotations(float yaw, float pitch) {
        SilentRotationsComponent component = Nonsense.component(SilentRotationsComponent.class);
        component.prevServerYaw = component.serverYaw;
        component.prevServerPitch = component.serverPitch;
        component.serverYaw = mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset = yaw;
        component.serverPitch = mc.thePlayer.rotationPitchHead = pitch;
    }

    public static Vec3 getLook(float delta, Entity entity) {
        SilentRotationsComponent component = Nonsense.component(SilentRotationsComponent.class);

        if (delta == 1.0F) {
            return entity.getVectorForRotation(component.serverPitch, component.serverYaw);
        }

        float pitch = MathUtil.lerp(component.prevServerPitch, component.serverPitch, delta);
        float yaw = MathUtil.lerp(component.prevServerYaw, component.serverYaw, delta);
        return entity.getVectorForRotation(pitch, yaw);
    }



}
