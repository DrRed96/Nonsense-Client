package wtf.bhopper.nonsense.component.impl.packet;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.component.AbstractComponent;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventJoinServer;

public class LastConnectionComponent extends AbstractComponent {

    private String ip;
    private int port;

    @EventLink
    public final Listener<EventJoinServer> onJoin = event -> {
        this.ip = event.ip;
        this.port = event.port;
    };

    public static String getIp() {
        return Nonsense.component(LastConnectionComponent.class).ip;
    }

    public static int getPort() {
        return Nonsense.component(LastConnectionComponent.class).port;
    }

}
