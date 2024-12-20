package wtf.bhopper.nonsense.event.impl.player;

import wtf.bhopper.nonsense.event.Cancellable;

public class EventChat extends Cancellable {

    public String message;

    public EventChat(String message) {
        this.message = message;
    }

}
