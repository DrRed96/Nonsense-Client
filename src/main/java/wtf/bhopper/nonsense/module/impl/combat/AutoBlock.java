package wtf.bhopper.nonsense.module.impl.combat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.interact.EventClickAction;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.player.RotationUtil;


@ModuleInfo(name = "Auto Block",
        description = "Automatically blocks your sword",
        category = ModuleCategory.COMBAT,
        searchAlias = "Block Hit")
public class AutoBlock extends Module {

    private final GroupProperty targetsGroup = new GroupProperty("Targets", "What entities Kill Aura should target", this);
    private final BooleanProperty players = new BooleanProperty("Players", "Target Players.", true);
    private final BooleanProperty mobs = new BooleanProperty("Mobs", "Target Mobs (Zombies, Skeletons, etc.)", false);
    private final BooleanProperty animals = new BooleanProperty("Animals", "Target Animals (Pigs, Cows, etc.)", false);
    private final BooleanProperty others = new BooleanProperty("Others", "Target other entities", false);
    private final BooleanProperty invis = new BooleanProperty("Invisible", "Target invisible entities", true);
    private final BooleanProperty dead = new BooleanProperty("Dead", "Target dead entities", false);
    private final BooleanProperty teams = new BooleanProperty("Teams", "Prevents you from attacking teammates", true);

    private final NumberProperty range = new NumberProperty("Range", "Auto block range", 7.0, 0.0, 16.0, 0.05, NumberProperty.FORMAT_DISTANCE);
    private final BooleanProperty auraOnly = new BooleanProperty("Kill Aura Only", "Only blocks when Kill Aura is enabled", true);


    public AutoBlock() {
        this.targetsGroup.addProperties(this.players, this.mobs, this.animals, this.others, this.invis, this.dead, this.teams);
        this.addProperties(this.targetsGroup, this.range, this.auraOnly);
    }

    @EventLink
    public final Listener<EventClickAction> onClick = event -> {
        if (!event.usingItem) {
            if (this.canBlock()) {
                event.right = true;
            }
        } else {
            if (event.releaseButton) {
                event.release = !this.canBlock();
            }
        }
    };

    public boolean canBlock() {
        return this.isToggled() && this.blockItem() &&
                !mc.theWorld.getEntities(EntityLivingBase.class, this::isValidTarget).isEmpty() &&
                (!this.auraOnly.get() || Nonsense.module(KillAura.class).isToggled());
    }

    private boolean blockItem() {
        ItemStack item = mc.thePlayer.getHeldItem();
        return item != null && item.getItemUseAction() == EnumAction.BLOCK;
    }

    private boolean isValidTarget(EntityLivingBase entity) {

        if (entity == null || entity == mc.thePlayer || entity.isFake) {
            return false;
        }

        if (Nonsense.module(AntiBot.class).isBot(entity)) {
            return false;
        }

        if (entity.isInvisible() && !this.invis.get()) {
            return false;
        }

        if (entity.isDead && !this.dead.get()) {
            return false;
        }

        if (RotationUtil.rayCastRange(entity.getPositionEyes(1.0F), mc.thePlayer.getEntityBoundingBox()) > this.range.get()) {
            return false;
        }

        return switch (entity) {
            case EntityPlayer player -> this.players.get() && (!this.teams.get() || PlayerUtil.isOnSameTeam(player));
            case EntityMob _ -> this.mobs.get();
            case EntityAnimal _ -> this.animals.get();
            default -> this.others.get();
        };
    }

}
