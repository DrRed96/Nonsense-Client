package wtf.bhopper.nonsense.component.impl.packet;

import net.minecraft.client.multiplayer.ServerAddress;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.component.AbstractComponent;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventPreMotion;
import wtf.bhopper.nonsense.util.misc.Stopwatch;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PingComponent extends AbstractComponent {

    private static final long TIMEOUT_AUTO_DISABLE = 120000L;
    private static final long DELAY = 10000L;
    private static final long DEFAULT_PING = 250L;

    private long ping = DEFAULT_PING;

    private final Executor thread = Executors.newFixedThreadPool(1);

    private final Stopwatch lastPing = new Stopwatch();
    private final Stopwatch lastGrab = new Stopwatch();

    @EventLink
    public final Listener<EventPreMotion> onPre = _ -> {
        if (this.lastPing.hasReached(DELAY) && !this.lastGrab.hasReached(TIMEOUT_AUTO_DISABLE)) {
            this.ping();
            this.lastPing.reset();
        }
    };

    private void ping() {
        if (mc.isIntegratedServerRunning()) {
            this.ping = 0L;
            return;
        }

        this.thread.execute(() -> {
            this.lastPing.reset();
            this.ping = new Pinger(LastConnectionComponent.getIp()).call();
        });
    }

    public static long getPing() {
        PingComponent component =  Nonsense.component(PingComponent.class);
        if (component.lastGrab.hasReached(TIMEOUT_AUTO_DISABLE)) {
            component.lastGrab.reset();
            return DEFAULT_PING;
        } else {
            component.lastGrab.reset();
            return component.ping;
        }
    }

    public static class Pinger implements Callable<Long> {
        private final SocketAddress address;

        public Pinger(String address) {
            ServerAddress serverAddress = ServerAddress.fromString(address);
            this.address = new InetSocketAddress(serverAddress.getIP(), serverAddress.getPort());
        }

        @Override
        public Long call() {
            try {
                Socket socket = new Socket();
                long time = System.currentTimeMillis();
                socket.connect(this.address);
                socket.close();
                return System.currentTimeMillis() - time;
            } catch (Exception _) {
                return 0L;
            }
        }
    }



}
