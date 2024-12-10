package wtf.bhopper.nonsense.util.minecraft;

import net.minecraft.network.Packet;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.EventPriorities;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventSendPacket;

import java.util.ArrayDeque;
import java.util.Queue;

public enum BlinkUtil {
    INSTANCE;

    private boolean blink = false;

    private final Queue<Packet<?>> chokedPackets = new ArrayDeque<>();

    @EventLink(EventPriorities.VERY_HIGH)
    public final Listener<EventSendPacket> onSendPacket = event -> {
        if (this.blink && !event.isCancelled()) {
            this.chokedPackets.add(event.packet);
            event.cancel();
        }
    };

    public static void enableBlink() {
        INSTANCE.blink = true;
    }

    public static void disableBlink() {
        disableBlink(true);
    }

    public static void disableBlink(boolean poll) {
        INSTANCE.blink = false;
        if (poll) {
            poll();
        } else {
            INSTANCE.chokedPackets.clear();
        }
    }

    public static boolean isBlinking() {
        return INSTANCE.blink;
    }

    public static void poll() {
        while (!INSTANCE.chokedPackets.isEmpty()) {
            PacketUtil.sendNoEvent(INSTANCE.chokedPackets.poll());
        }
    }

    public static void init() {
        Nonsense.getEventBus().subscribe(INSTANCE);
    }

}
