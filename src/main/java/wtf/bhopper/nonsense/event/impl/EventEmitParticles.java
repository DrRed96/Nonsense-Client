package wtf.bhopper.nonsense.event.impl;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import wtf.bhopper.nonsense.event.Cancellable;

public class EventEmitParticles extends Cancellable {

    public Entity entity;
    public EnumParticleTypes particleType;
    public int amount;

    public EventEmitParticles(Entity entity, EnumParticleTypes particleType, int amount) {
        this.entity = entity;
        this.particleType = particleType;
        this.amount = amount;
    }

}
