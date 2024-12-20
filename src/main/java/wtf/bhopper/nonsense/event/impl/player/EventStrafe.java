package wtf.bhopper.nonsense.event.impl.player;

import wtf.bhopper.nonsense.event.Event;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;

public class EventStrafe implements Event, MinecraftInstance {

    public float forward;
    public float strafe;
    public float friction;
    public float yaw;

    public EventStrafe(float forward, float strafe, float friction, float yaw) {
        this.forward = forward;
        this.strafe = strafe;
        this.friction = friction;
        this.yaw = yaw;
    }

    public void setSpeed(double speed, double motionMultiplier) {
        this.friction = (this.forward != 0.0F && this.strafe != 0.0F) ? (float)speed * 0.98F : (float)speed;
        mc.thePlayer.motionX *= motionMultiplier;
        mc.thePlayer.motionZ *= motionMultiplier;
    }

    public void setSpeed(double speed) {
        this.friction = (this.forward != 0.0F && this.strafe != 0.0F) ? (float)speed * 0.98F : (float)speed;
        mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
    }

}
