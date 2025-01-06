package wtf.bhopper.nonsense.component.impl.packet;

import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.component.Component;
import wtf.bhopper.nonsense.event.Cancellable;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventWorldChange;
import wtf.bhopper.nonsense.event.impl.packet.EventReceivePacket;
import wtf.bhopper.nonsense.event.impl.packet.EventSendPacket;
import wtf.bhopper.nonsense.event.impl.player.EventPostMotion;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;
import wtf.bhopper.nonsense.util.misc.Stopwatch;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

// Dear Billionaire,
// I'm stealing your Ping Spoof Component and there's nothing you can do to stop me :smiling_imp:
// Love from Calculus <3

public class PingSpoofComponent extends Component {

    private final ConcurrentLinkedQueue<PacketUtil.TimedPacket> packets = new ConcurrentLinkedQueue<>();
    private final Stopwatch enabledTimer = new Stopwatch();
    private boolean enabled;
    private long amount;

    private final PacketGroup regular = new PacketGroup(C0FPacketConfirmTransaction.class, C00PacketKeepAlive.class, S1CPacketEntityMetadata.class);
    private final PacketGroup velocity = new PacketGroup(S12PacketEntityVelocity.class, S27PacketExplosion.class);
    private final PacketGroup teleports = new PacketGroup(S08PacketPlayerPosLook.class, S39PacketPlayerAbilities.class, S09PacketHeldItemChange.class);
    private final PacketGroup players = new PacketGroup(S13PacketDestroyEntities.class, S14PacketEntity.class, S14PacketEntity.S16PacketEntityLook.class, S14PacketEntity.S15PacketEntityRelMove.class, S14PacketEntity.S17PacketEntityLookMove.class, S18PacketEntityTeleport.class, S20PacketEntityProperties.class, S19PacketEntityHeadLook.class);
    private final PacketGroup blink = new PacketGroup(C02PacketUseEntity.class, C0DPacketCloseWindow.class, C0EPacketClickWindow.class, C0CPacketInput.class, C0BPacketEntityAction.class, C08PacketPlayerBlockPlacement.class, C07PacketPlayerDigging.class, C09PacketHeldItemChange.class, C13PacketPlayerAbilities.class, C15PacketClientSettings.class, C16PacketClientStatus.class, C17PacketCustomPayload.class, C18PacketSpectate.class, C19PacketResourcePackStatus.class, C03PacketPlayer.class, C03PacketPlayer.C04PacketPlayerPosition.class, C03PacketPlayer.C05PacketPlayerLook.class, C03PacketPlayer.C06PacketPlayerPosLook.class, C0APacketAnimation.class);
    private final PacketGroup movement = new PacketGroup(C03PacketPlayer.class, C03PacketPlayer.C04PacketPlayerPosition.class, C03PacketPlayer.C05PacketPlayerLook.class, C03PacketPlayer.C06PacketPlayerPosLook.class);

    private final PacketGroup[] types = new PacketGroup[]{ regular, velocity, teleports, players, blink, movement };

    public Cancellable onPacket(Packet<?> packet, Cancellable event) {
        if (!event.isCancelled() && enabled && Arrays.stream(types).anyMatch(group -> group.enabled && Arrays.stream(group.packets).anyMatch(p -> p == packet.getClass()))) {
            event.cancel();
            packets.add(new PacketUtil.TimedPacket(packet));
        }

        return event;
    }

    public static void dispatch() {

        PingSpoofComponent component = Nonsense.component(PingSpoofComponent.class);

        if (!component.packets.isEmpty()) {
            // Stops the packets from being called twice
            boolean enabled = component.enabled;
            component.enabled = false;
            component.packets.forEach(timedPacket -> PacketUtil.queue(timedPacket.getPacket()));
            component.enabled = enabled;
            component.packets.clear();
        }
    }

    public static void disable() {
        PingSpoofComponent component = Nonsense.component(PingSpoofComponent.class);
        component.enabled = false;
        component.enabledTimer.setTime(component.enabledTimer.getTime() - 999999999);
    }

    @EventLink
    public final Listener<EventSendPacket> onSend = event -> event.setCancelled(this.onPacket(event.packet, event).isCancelled());

    @EventLink
    public final Listener<EventReceivePacket> onReceive = event -> event.setCancelled(this.onPacket(event.packet, event).isCancelled());

    @EventLink
    public final Listener<EventWorldChange> onWorldChange = _ -> dispatch();

    @EventLink
    public final Listener<EventPostMotion> onPost = event -> {

        if (!(this.enabled = !this.enabledTimer.hasReached(100) && !(mc.currentScreen instanceof GuiDownloadTerrain))) {
            dispatch();
            return;
        }

        this.enabled = false;

        this.packets.forEach(packet -> {
            if (packet.getTime() + this.amount < System.currentTimeMillis()) {
                PacketUtil.queue(packet.getPacket());
                this.packets.remove(packet);
            }
        });

        this.enabled = true;

    };

    public static void spoof(int amount, boolean regular, boolean velocity, boolean teleports, boolean players) {
        spoof(amount, regular, velocity, teleports, players, false);
    }

    public static void spoof(int amount, boolean regular, boolean velocity, boolean teleports, boolean players, boolean blink, boolean movement) {

        PingSpoofComponent component = Nonsense.component(PingSpoofComponent.class);

        component.enabledTimer.reset();

        component.regular.enabled = regular;
        component.velocity.enabled = velocity;
        component.teleports.enabled = teleports;
        component.players.enabled = players;
        component.blink.enabled = blink;
        component.movement.enabled = movement;
        
        component.amount = amount;
    }

    public static void spoof(int amount, boolean regular, boolean velocity, boolean teleports, boolean players, boolean blink) {
        spoof(amount, regular, velocity, teleports, players, blink, false);
    }

    public static void blink() {
        spoof(9999999, true, false, false, false, true);
    }

    public static class PacketGroup {
        public boolean enabled;
        public final Class[] packets;

        public PacketGroup(Class... packets) {
            this.enabled = false;
            this.packets = packets;
        }

    }

}
