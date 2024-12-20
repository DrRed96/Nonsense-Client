package wtf.bhopper.nonsense.event.impl.render;

import wtf.bhopper.nonsense.event.Event;

public class EventPostRenderWorld implements Event {

    public final float delta;

    public EventPostRenderWorld(float delta) {
        this.delta = delta;
    }

}
