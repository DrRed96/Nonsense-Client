package wtf.bhopper.nonsense.module.impl.other;

import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.packet.EventReceivePacket;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;

@ModuleInfo(name = "Pack Spoofer",
        description = "Spoofs server resource packs",
        category = ModuleCategory.OTHER)
public class PackSpoofer extends AbstractModule {

    @EventLink
    public final Listener<EventReceivePacket> onReceivePacket = event -> {
        if (event.packet instanceof S48PacketResourcePackSend packet) {
            event.cancel();
            PacketUtil.send(new C19PacketResourcePackStatus(packet.getHash(), C19PacketResourcePackStatus.Action.ACCEPTED));
            PacketUtil.send(new C19PacketResourcePackStatus(packet.getHash(), C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
            Notification.send("Pack Spoofer", "Spoofed Pack: " + packet.getURL(), NotificationType.SUCCESS, 3000);
        }
    };

}
