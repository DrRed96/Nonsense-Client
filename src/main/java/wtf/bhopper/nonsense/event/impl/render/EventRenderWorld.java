package wtf.bhopper.nonsense.event.impl.render;

import wtf.bhopper.nonsense.event.Event;

public class EventRenderWorld implements Event {

    public final float delta;

    public EventRenderWorld(float delta) {
        this.delta = delta;
    }

}
