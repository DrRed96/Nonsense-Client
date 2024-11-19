package wtf.bhopper.nonsense.util.minecraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.Vec3;

public class PlayerUtil implements MinecraftInstance {

    public static boolean canUpdate() {
        return mc.thePlayer != null && mc.theWorld != null;
    }

    public static Vec3 eyesPos() {
        return new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    }

    public static void swing(boolean silent) {
        if (silent) {
            PacketUtil.send(new C0APacketAnimation());
        } else {
            mc.thePlayer.swingItem();
        }
    }

    public static boolean isOnSameTeam(final EntityPlayer player) {

        if (player == null) {
            return false;
        }

        if (player.getTeam() != null && mc.thePlayer.getTeam() != null) {
            final char c1 = player.getDisplayName().getFormattedText().charAt(1);
            final char c2 = mc.thePlayer.getDisplayName().getFormattedText().charAt(1);
            return c1 == c2;
        }
        return false;
    }

}
