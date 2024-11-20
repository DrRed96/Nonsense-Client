package wtf.bhopper.nonsense.event.impl;

import wtf.bhopper.nonsense.event.Event;

public class EventRender3D implements Event {

    public final float delta;

    public EventRender3D(float delta) {
        this.delta = delta;
    }

}
