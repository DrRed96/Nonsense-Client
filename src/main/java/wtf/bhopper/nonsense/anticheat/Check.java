package wtf.bhopper.nonsense.anticheat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S14PacketEntity;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;

import java.util.*;

public abstract class Check implements MinecraftInstance {

    public final String name = this.getClass().getAnnotation(CheckInfo.class).name();
    public final String classifier = this.getClass().getAnnotation(CheckInfo.class).classifier();
    public final String description = this.getClass().getAnnotation(CheckInfo.class).description();
    public final int maxViolations = this.getClass().getAnnotation(CheckInfo.class).maxViolations();
    public final boolean unreliable = this.getClass().getAnnotation(CheckInfo.class).unreliable();

    private final Map<UUID, Integer> violations = new HashMap<>();

    protected abstract CheckResult check(EntityPlayer player, PlayerData data, Packet<?> packet);

    public boolean performCheckAndUpdate(EntityPlayer player, PlayerData data, Packet<?> packet) {
        switch (this.check(player, data, packet)) {
            case VIOLATE -> {
                data.violationLevel++;
                this.violations.put(player.getUniqueID(), this.violations.getOrDefault(player.getUniqueID(), 0) + 1);
                if (this.violations.get(player.getUniqueID()) >= this.maxViolations) {
                    Nonsense.getAntiCheat().flag(player);
                }
                Nonsense.getAntiCheat().notifyViolation(player, data, this, this.violations.get(player.getUniqueID()));
                return true;
            }
            case IDLE -> { /* Does nothing */ }
            case RESET -> this.violations.put(player.getUniqueID(), 0);
        }

        return false;
    }

    public void reset() {
        this.violations.clear();
    }

    public String displayName() {
        return this.name + " " + this.classifier;
    }

    protected static boolean isUpdate(EntityPlayer player, Packet<?> packet) {
        return packet instanceof S14PacketEntity p && p.getEntity(mc.theWorld) == player;
    }

}
