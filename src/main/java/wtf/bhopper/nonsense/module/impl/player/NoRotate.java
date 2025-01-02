package wtf.bhopper.nonsense.module.impl.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventPreMotion;
import wtf.bhopper.nonsense.event.impl.packet.EventReceivePacket;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.player.Rotation;

@ModuleInfo(name = "No Rotate",
        description = "Prevents the server from modifying your rotations",
        category = ModuleCategory.PLAYER)
public class NoRotate extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "No rotate method", Mode.VANILLA);

    private Rotation rotation = null;

    public NoRotate() {
        this.addProperties(this.mode);
    }

    @EventLink
    public final Listener<EventReceivePacket> onReceivePacket = event -> {
        if (!PlayerUtil.canUpdate()) {
            return;
        }

        if (event.packet instanceof S08PacketPlayerPosLook packet) {
            packet.setRotations(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);

            switch (this.mode.get()) {
                case EDIT -> this.rotation = new Rotation(packet.getYaw(), packet.getPitch());
                case PACKET -> PacketUtil.send(new C03PacketPlayer.C05PacketPlayerLook(packet.getYaw(), packet.getPitch(), mc.thePlayer.onGround));
            }
        }
    };

    @EventLink
    public final Listener<EventPreMotion> onPre = event -> {
        if (this.rotation != null) {
            event.setRotations(this.rotation);
            this.rotation = null;
        }
    };

    private enum Mode {
        VANILLA,
        EDIT,
        PACKET
    }

}
