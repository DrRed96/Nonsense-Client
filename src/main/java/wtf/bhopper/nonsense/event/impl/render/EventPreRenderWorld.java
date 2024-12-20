package wtf.bhopper.nonsense.event.impl.render;

import wtf.bhopper.nonsense.event.Event;

public class EventPreRenderWorld implements Event {

    public final float delta;

    public EventPreRenderWorld(float delta) {
        this.delta = delta;
    }

}
