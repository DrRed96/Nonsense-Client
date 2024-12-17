package wtf.bhopper.nonsense.util.minecraft.player;

import com.google.common.base.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
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

    public Rotation(Entity entity) {
        this.yaw = MathHelper.wrapAngleTo180_float(entity.rotationYaw);
        this.pitch = entity.rotationPitch;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("yaw", this.yaw)
                .add("pitch", this.pitch)
                .toString();
    }
}
