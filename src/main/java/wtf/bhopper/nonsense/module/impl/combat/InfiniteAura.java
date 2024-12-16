package wtf.bhopper.nonsense.module.impl.combat;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventTick;
import wtf.bhopper.nonsense.event.impl.EventUpdate;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.PacketUtil;
import wtf.bhopper.nonsense.util.minecraft.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.RotationUtil;
import wtf.bhopper.nonsense.util.misc.Clock;
import wtf.bhopper.nonsense.util.misc.MathUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ModuleInfo(name = "Infinite Aura", description = "Kill Aura with unlimited range", category = ModuleCategory.COMBAT)
public class InfiniteAura extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Method for TP aura", Mode.INSTANT);
    private final EnumProperty<Sorting> sorting = new EnumProperty<>("Sorting", "Target sorting method", Sorting.FOV);
    private final NumberProperty minAps = new NumberProperty("Min APS", "Minimum attacks per second", 7.0, 1.0, 20.0, 1.0, NumberProperty.FORMAT_APS);
    private final NumberProperty maxAps = new NumberProperty("Max APS", "Maximum attacks per second", 12.0, 1.0, 20.0, 1.0, NumberProperty.FORMAT_APS);

    private final GroupProperty targetsGroup = new GroupProperty("Targets", "What entities Kill Aura should target", this);
    private final BooleanProperty players = new BooleanProperty("Players", "Target Players.", true);
    private final BooleanProperty mobs = new BooleanProperty("Mobs", "Target Mobs (Zombies, Skeletons, etc.)", false);
    private final BooleanProperty animals = new BooleanProperty("Animals", "Target Animals (Pigs, Cows, etc.)", false);
    private final BooleanProperty others = new BooleanProperty("Others", "Target other entities", false);
    private final BooleanProperty invis = new BooleanProperty("Invisible", "Target invisible entities", true);
    private final BooleanProperty dead = new BooleanProperty("Dead", "Target dead entities", false);
    private final BooleanProperty teams = new BooleanProperty("Teams", "Prevents you from attacking teammates", true);
    private final NumberProperty existed = new NumberProperty("Ticks Existed", "Ticks and entity has to have existed for before attacking.", 20, 0, 50, 1, NumberProperty.FORMAT_INT);

    private final NumberProperty range = new NumberProperty("Range", "Attack range", 100.0, 25.0, 500.0, 25.0, NumberProperty.FORMAT_DISTANCE);
    private final BooleanProperty autoDisable = new BooleanProperty("Auto Disable", "Automatically disabled Kill Aura in certain situations", true);
    private final BooleanProperty particles = new BooleanProperty("Particles", "Renderers particles when attacking", true);

    private EntityLivingBase target = null;

    private int nextDelay = -1;
    private final Clock attackTimer = new Clock();

    public InfiniteAura() {
        this.targetsGroup.addProperties(this.players, this.mobs, this.animals, this.others, this.invis, this.dead, this.teams, this.existed);
        this.addProperties(this.mode, this.sorting, this.minAps, this.maxAps, this.targetsGroup, this.range, this.autoDisable, this.particles);
        this.setSuffix(this.mode::getDisplayValue);

        this.minAps.addValueChangeListener((oldValue, value) -> {
            if (this.maxAps.getDouble() < value) {
                this.maxAps.set(value);
            }
        });

        this.maxAps.addValueChangeListener((oldValue, value) -> {
            if (this.minAps.getDouble() > value) {
                this.minAps.set(value);
            }
        });

    }

    @EventLink
    public final Listener<EventTick> onTick = _ -> {

        if (!PlayerUtil.canUpdate()) {
            return;
        }

        if (this.autoDisable.get() && mc.thePlayer.isDead) {
            this.toggle(false);
            Notification.send("Death", "Kill Aura was automatically disabled", NotificationType.WARNING, 3000);
            return;
        }

        if (this.nextDelay == -1) {
            this.nextDelay = (int)(1000.0F / (float) ThreadLocalRandom.current().nextInt(this.minAps.getInt(), this.maxAps.getInt() + 1));
        }

        this.updateTarget();

    };

    @SuppressWarnings("ConstantValue")
    @EventLink
    public final Listener<EventUpdate> onUpdate = _ -> {

        if (this.target != null && this.target.hurtResistantTime <= 15) {

            List<Vec3> path = this.getPath(this.target);


            if (path == null) {
                return;
            }

            for (int i = 1; i < path.size(); i++) {
                Vec3 point = path.get(i);
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(point.xCoord, point.yCoord, point.zCoord, mc.thePlayer.onGround));
            }

            // Temporarily set the players position to the spoofed position so module like critical's don't mess up the attack
            Vec3 prevPos = mc.thePlayer.getPositionVector();
            mc.thePlayer.setPosition(path.getLast().xCoord, path.getLast().yCoord, path.getLast().zCoord);

            PacketUtil.leftClickPackets(new MovingObjectPosition(this.target, MathUtil.closestPoint(this.target.getEntityBoundingBox(), PlayerUtil.eyesPos())), true);
            if (this.particles.get()) {
                if (Nonsense.module(Criticals.class).isToggled() || (mc.thePlayer.fallDistance > 0.0F && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isPotionActive(Potion.blindness) && mc.thePlayer.ridingEntity == null)) {
                    mc.thePlayer.onCriticalHit(this.target);
                }

                if (EnchantmentHelper.getLivingModifier(mc.thePlayer.getHeldItem(), this.target.getCreatureAttribute()) > 0.0F) {
                    mc.thePlayer.onEnchantmentCritical(this.target);
                }

            }

            mc.thePlayer.setPosition(prevPos.xCoord, prevPos.yCoord, prevPos.zCoord);

            for (int i = path.size() - 2; i > 0; i--) {
                Vec3 point = path.get(i);
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(point.xCoord, point.yCoord, point.zCoord, mc.thePlayer.onGround));
            }
        }

        this.attackTimer.reset();
        this.nextDelay = 20 / (int) ((Math.random() * (this.maxAps.getDouble() - this.minAps.getDouble())) + this.minAps.getDouble());

    };

    private List<Vec3> getPath(EntityLivingBase target) {

        List<Vec3> path = new ArrayList<>();

        switch (this.mode.get()) {
            case INSTANT -> {
                path.add(mc.thePlayer.getPositionVector());
                path.add(target.getPositionVector());
            }
        }

        return path;
    }

    private void updateTarget() {
        List<EntityLivingBase> entities = mc.theWorld.getEntities(EntityLivingBase.class, this::isTargetValid);

        if (entities.isEmpty()) {
            this.target = null;
            return;
        }

        entities.sort(switch (this.sorting.get()) {
            case ANGLE -> Comparator.comparingDouble(entity -> RotationUtil.getRotations(entity).yaw);
            case RANGE -> Comparator.comparingDouble(entity -> entity.getDistanceToEntity(mc.thePlayer));
            case FOV -> Comparator.comparingDouble(entity -> Math.abs(RotationUtil.getYawChange(entity.posX, entity.posZ)));
            case ARMOR -> Comparator.comparingInt(entity -> entity instanceof EntityPlayer ? ((EntityPlayer) entity).inventory.getTotalArmorValue() : (int) entity.getHealth());
            case HEALTH -> Comparator.comparingDouble(EntityLivingBase::getHealth);
        });

        this.target = entities.getFirst();
    }

    private boolean isTargetValid(EntityLivingBase target) {
        if (target == null || target.isClientPlayer()) {
            return false;
        }

        if ((target.getHealth() <= 0.0F || target.isDead) && !this.dead.get()) {
            return false;
        }

        if (target.ticksExisted < this.existed.getInt()) {
            return false;
        }

        if (Nonsense.module(AntiBot.class).isBot(target)) {
            return false;
        }

        Vec3 hitVec = MathUtil.closestPoint(target.getEntityBoundingBox(), PlayerUtil.eyesPos());
        double dist = hitVec.distanceTo(PlayerUtil.eyesPos());

        if (dist > this.range.getDouble()) {
            return false;
        }

        if (target instanceof EntityPlayer player) {

            if (!this.players.get()) {
                return false;
            }

            if (player.isPlayerSleeping()) {
                return false;
            }

            if (this.teams.get() && PlayerUtil.isOnSameTeam(player)) {
                return false;
            }

            if (!this.invis.get() && target.isInvisible()) {
                return false;
            }

            return true;
        }

        if (target instanceof EntityMob || target instanceof EntitySlime) {
            return this.mobs.get();
        } else if (target instanceof EntityAnimal) {
            return this.animals.get();
        } else {
            return this.others.get();
        }

    }

    public EntityLivingBase getTarget() {
        return this.target;
    }

    private enum Mode {
        INSTANT
        // TODO: add path find mode
    }

    private enum Sorting {
        ANGLE,
        RANGE,
        @DisplayName("FOV") FOV,
        ARMOR,
        HEALTH,
    }

}
