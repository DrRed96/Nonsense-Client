package wtf.bhopper.nonsense.event.impl;

import net.minecraft.entity.EntityLivingBase;
import wtf.bhopper.nonsense.event.Event;

public class EventPostRenderEntity implements Event {

    public final EntityLivingBase entity;
    public final float limbSwing;
    public final float limbSwingAmount;
    public final float ageInTicks;
    public final float rotationYawHead;
    public final float rotationPitch;
    public final float chestRot;
    public final float offset;

    public EventPostRenderEntity(EventPreRenderEntity pre) {
        this.entity = pre.entity;
        this.limbSwing = pre.limbSwing;
        this.limbSwingAmount = pre.limbSwingAmount;
        this.ageInTicks = pre.ageInTicks;
        this.rotationYawHead = pre.rotationYawHead;
        this.rotationPitch = pre.rotationPitch;
        this.chestRot = pre.chestRot;
        this.offset = pre.offset;
    }
}
