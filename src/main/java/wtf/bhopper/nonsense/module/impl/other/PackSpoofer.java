package wtf.bhopper.nonsense.module.impl.other;

import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventReceivePacket;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.util.minecraft.PacketUtil;

@ModuleInfo(name = "Pack Spoofer", description = "Spoofs server resource packs", category = ModuleCategory.OTHER)
public class PackSpoofer extends Module {

    @EventLink
    public final Listener<EventReceivePacket> onReceivePacket = event -> {
        if (event.packet instanceof S48PacketResourcePackSend packet) {
            event.cancel();
            PacketUtil.send(new C19PacketResourcePackStatus(packet.getHash(), C19PacketResourcePackStatus.Action.ACCEPTED));
            PacketUtil.send(new C19PacketResourcePackStatus(packet.getHash(), C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
        }
    };

}
