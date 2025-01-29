package wtf.bhopper.nonsense.component.impl.packet;

import net.minecraft.network.Packet;
import wtf.bhopper.nonsense.component.AbstractComponent;
import wtf.bhopper.nonsense.event.EventPriorities;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.packet.EventSendPacket;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;

import java.util.ArrayDeque;
import java.util.Queue;

public class BlinkComponent extends AbstractComponent {

    private boolean blink = false;

    private final Queue<Packet<?>> chokedPackets = new ArrayDeque<>();

    @EventLink(EventPriorities.VERY_HIGH)
    public final Listener<EventSendPacket> onSendPacket = event -> {
        if (this.blink && !event.isCancelled()) {
            this.chokedPackets.add(event.packet);
            event.cancel();
        }
    };

    @EventLink
    public final Listener<EventTick> onTick = _ -> {
        if (!PlayerUtil.canUpdate() && this.blink) {
            this.blink = false;
            this.chokedPackets.clear();
        }
    };

    public void enableBlink() {
        this.blink = true;
    }

    public void disableBlink() {
        disableBlink(true);
    }

    public void disableBlink(boolean poll) {
        this.blink = false;
        if (poll) {
            poll();
        } else {
            this.chokedPackets.clear();
        }
    }

    public boolean isBlinking() {
        return this.blink;
    }

    public void poll() {
        while (!this.chokedPackets.isEmpty()) {
            PacketUtil.sendNoEvent(this.chokedPackets.poll());
        }
    }

}
