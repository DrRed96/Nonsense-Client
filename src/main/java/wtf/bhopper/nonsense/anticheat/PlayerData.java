package wtf.bhopper.nonsense.anticheat;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerData {

    public int violationLevel = 0;
    public int chatLine = -1;

    public double moveX, moveY, moveZ;

    public void update(EntityPlayer player) {
        this.moveX = player.posX - player.lastTickPosX;
        this.moveY = player.posY - player.lastTickPosY;
        this.moveZ = player.posZ - player.lastTickPosZ;
    }

}
