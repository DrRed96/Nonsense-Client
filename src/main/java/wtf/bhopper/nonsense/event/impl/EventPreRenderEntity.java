package wtf.bhopper.nonsense.event.impl;

import net.minecraft.entity.EntityLivingBase;
import wtf.bhopper.nonsense.event.Cancellable;

public class EventPreRenderEntity extends Cancellable {

    public final EntityLivingBase entity;
    public final float limbSwing;
    public final float limbSwingAmount;
    public final float ageInTicks;
    public final float rotationYawHead;
    public final float rotationPitch;
    public final float chestRot;
    public final float offset;

    public EventPreRenderEntity(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYawHead, float rotationPitch, float chestRot, float offset) {
        this.entity = entity;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.rotationYawHead = rotationYawHead;
        this.rotationPitch = rotationPitch;
        this.chestRot = chestRot;
        this.offset = offset;
    }

}
