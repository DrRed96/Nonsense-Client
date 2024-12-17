package wtf.bhopper.nonsense.module.impl.other;

import net.minecraft.network.play.server.S29PacketSoundEffect;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventReceivePacket;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

@ModuleInfo(name = "Lightning Detector",
        description = "Attempts to detect lightning strikes",
        category = ModuleCategory.OTHER)
public class LightningDetector extends Module {

    @EventLink
    public final Listener<EventReceivePacket> onReceivePacket = event -> {
        if (event.packet instanceof S29PacketSoundEffect packet) {
            if (packet.getSoundName().equalsIgnoreCase("ambient.weather.thunder")) {
                int x = (int)packet.getX();
                int z = (int)packet.getZ();
                int dist = (int)Math.hypot(mc.thePlayer.posX - x, mc.thePlayer.posZ - z);
                ChatUtil.info("Detected lightning at: %,d, %,d. (%,d blocks away)", x, z, dist);
            }
        }
    };

}
