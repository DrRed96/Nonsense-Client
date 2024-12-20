package wtf.bhopper.nonsense.event.impl.packet;

import net.minecraft.network.Packet;
import wtf.bhopper.nonsense.event.Cancellable;

public class EventReceivePacket extends Cancellable {

    public Packet packet;

    public EventReceivePacket(Packet<?> packet) {
        this.packet = packet;
    }

}
