package wtf.bhopper.nonsense.util.minecraft;

import net.minecraft.util.Vec3;

public class Rotation {

    public float yaw;
    public float pitch;

    public Vec3 hitVec;

    public Rotation() {
        this.yaw = 0.0F;
        this.pitch = 0.0F;
        this.hitVec = null;
    }

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.hitVec = null;
    }

    public Rotation(float yaw, float pitch, Vec3 hitVec) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.hitVec = hitVec;
    }


}
