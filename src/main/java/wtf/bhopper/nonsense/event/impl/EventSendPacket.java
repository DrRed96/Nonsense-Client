package wtf.bhopper.nonsense.event.impl;

import net.minecraft.network.Packet;
import wtf.bhopper.nonsense.event.Cancellable;

public class EventSendPacket extends Cancellable {

    public Packet<?> packet;

    public EventSendPacket(Packet<?> packet) {
        this.packet = packet;
    }

}
