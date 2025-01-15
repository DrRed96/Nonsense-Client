package wtf.bhopper.nonsense.anticheat.check;

import net.minecraft.network.play.server.*;
import wtf.bhopper.nonsense.anticheat.PlayerData;
import wtf.bhopper.nonsense.util.minecraft.IMinecraft;

public abstract class Check implements IMinecraft {

    public final String name;
    public final String description;
    public final int maxVl;

    public Check() {
        this.name = this.getClass().getAnnotation(CheckInfo.class).name();
        this.description = this.getClass().getAnnotation(CheckInfo.class).description();
        this.maxVl = this.getClass().getAnnotation(CheckInfo.class).maxVl();
    }

    public void handleRelMove(PlayerData data, S14PacketEntity packet) { }
    public void handleTeleport(PlayerData data, S18PacketEntityTeleport packet) { }
    public void handleAnimation(PlayerData data, S0BPacketAnimation packet) { }
    public void handleEquipment(PlayerData data, S04PacketEntityEquipment packet) { }
    public void handleHeadLook(PlayerData data, S19PacketEntityHeadLook packet) { }
    public void handleEntityMetadata(PlayerData data, S1CPacketEntityMetadata packet) { }
    public void handleBlockBreakAnim(PlayerData data, S25PacketBlockBreakAnim packet) { }

}
