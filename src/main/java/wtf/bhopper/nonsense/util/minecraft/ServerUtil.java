package wtf.bhopper.nonsense.util.minecraft;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;

public class ServerUtil implements MinecraftInstance {

    public static String lastServer = null;
    public static int lastPort = 0;

    public static boolean isInTab(EntityPlayer player) {
        for (NetworkPlayerInfo info : mc.getNetHandler().getPlayerInfoMap()) {
            if (info.getGameProfile().getId().compareTo(player.getUniqueID()) == 0)  {
                return true;
            }
        }

        return false;
    }

}
