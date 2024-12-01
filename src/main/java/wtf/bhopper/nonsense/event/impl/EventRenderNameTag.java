package wtf.bhopper.nonsense.event.impl;

import net.minecraft.entity.Entity;
import wtf.bhopper.nonsense.event.Cancellable;

public class EventRenderNameTag extends Cancellable {

    public final Entity entity;
    public String name;

    public EventRenderNameTag(Entity entity, String name) {
        this.entity = entity;
        this.name = name;
    }

}
