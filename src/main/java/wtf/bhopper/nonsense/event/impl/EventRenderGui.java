package wtf.bhopper.nonsense.event.impl;

import net.minecraft.client.gui.ScaledResolution;
import wtf.bhopper.nonsense.event.Event;

public class EventRenderGui implements Event {

    public final ScaledResolution scale;
    public final float delta;

    public EventRenderGui(ScaledResolution scale, float delta) {
        this.scale = scale;
        this.delta = delta;
    }

}
