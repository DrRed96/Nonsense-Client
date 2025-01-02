package wtf.bhopper.nonsense.event.impl.client;

import wtf.bhopper.nonsense.event.Event;

public class EventJoinServer implements Event {

    public final String ip;
    public final int port;

    public EventJoinServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

}
