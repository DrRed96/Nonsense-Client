package wtf.bhopper.nonsense.module.impl.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import wtf.bhopper.nonsense.component.impl.player.RotationsComponent;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.packet.EventReceivePacket;
import wtf.bhopper.nonsense.event.impl.player.EventUpdate;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.player.Rotation;

@ModuleInfo(name = "No Rotate",
        description = "Prevents the server from modifying your rotations",
        category = ModuleCategory.PLAYER)
public class NoRotate extends AbstractModule {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "No rotate method", Mode.VANILLA);

    private Rotation rotation = null;

    public NoRotate() {
        super();
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
    public final Listener<EventUpdate> onPre = event -> {
        if (this.rotation != null) {
            RotationsComponent.updateServerRotations(this.rotation);
            this.rotation = null;
        }
    };

    private enum Mode {
        VANILLA,
        EDIT,
        PACKET
    }

}
