package wtf.bhopper.nonsense.anticheat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;

public class PlayerData {

    public int violationLevel = 0;
    public final int chatLine;

    public double moveX, moveY, moveZ;
    public double prevMoveX, prevMoveY, prevMoveZ;
    public float forward, strafe;

    public PlayerData(int chatLine) {
        this.chatLine = chatLine;
    }

    public void update(EntityPlayer player) {
        this.prevMoveX = this.moveX;
        this.prevMoveY = this.moveY;
        this.prevMoveZ = this.moveZ;
        this.moveX = player.posX - player.lastTickPosX;
        this.moveY = player.posY - player.lastTickPosY;
        this.moveZ = player.posZ - player.lastTickPosZ;

        float[] strafeValues = MoveUtil.calculateForwardStrafe(this.moveX, this.moveZ, player.rotationYaw);
        this.forward = strafeValues[0];
        this.strafe = strafeValues[1];
    }

    public void onPacket(Packet<?> packet) {
        // TODO: idk
    }

}
