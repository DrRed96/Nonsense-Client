package wtf.bhopper.nonsense.anticheat;

import net.minecraft.entity.player.EntityPlayer;
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

    protected abstract CheckResult performCheck(EntityPlayer player, PlayerData data);

    public boolean check(EntityPlayer player, PlayerData data) {
        switch (this.performCheck(player, data)) {
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

}
