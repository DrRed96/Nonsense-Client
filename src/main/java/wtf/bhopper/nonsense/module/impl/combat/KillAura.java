package wtf.bhopper.nonsense.module.impl.combat;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
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
import wtf.bhopper.nonsense.util.minecraft.Rotation;
import wtf.bhopper.nonsense.util.minecraft.RotationUtil;
import wtf.bhopper.nonsense.util.misc.Clock;
import wtf.bhopper.nonsense.util.misc.MathUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ModuleInfo(name = "Kill Aura",
        description = "Automatically attacks nearby entities",
        category = ModuleCategory.COMBAT)
public class KillAura extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Target selection method", Mode.SWITCH);
    private final EnumProperty<Sorting> sorting = new EnumProperty<>("Sorting", "Target sorting method", Sorting.RANGE);
    private final NumberProperty minAps = new NumberProperty("Min APS", "Minimum attacks per second", 7.0, 1.0, 20.0, 1.0, NumberProperty.FORMAT_APS);
    private final NumberProperty maxAps = new NumberProperty("Max APS", "Maximum attacks per second", 12.0, 1.0, 20.0, 1.0, NumberProperty.FORMAT_APS);

    private final GroupProperty targetsGroup = new GroupProperty("Targets", "What entities Kill Aura should target");
    private final BooleanProperty players = new BooleanProperty("Players", "Target Players.", true);
    private final BooleanProperty mobs = new BooleanProperty("Mobs", "Target Mobs (Zombies, Skeletons, etc.)", false);
    private final BooleanProperty animals = new BooleanProperty("Animals", "Target Animals (Pigs, Cows, etc.)", false);
    private final BooleanProperty others = new BooleanProperty("Others", "Target other entities", false);
    private final BooleanProperty invis = new BooleanProperty("Invisible", "Target invisible entities", true);
    private final BooleanProperty dead = new BooleanProperty("Dead", "Target dead entities", false);
    private final BooleanProperty teams = new BooleanProperty("Teams", "Prevents you from attacking teammates", true);
    private final NumberProperty existed = new NumberProperty("Ticks Existed", "Ticks and entity has to have existed for before attacking.", 20, 0, 50, 1, NumberProperty.FORMAT_INT);

    private final GroupProperty rangeGroup = new GroupProperty("Range", "Kill Aura range");
    private final NumberProperty playerRange = new NumberProperty("Players", "Player attack range", 4.0, 3.0, 10.0, 0.05, NumberProperty.FORMAT_DISTANCE);
    private final NumberProperty otherRange = new NumberProperty("Others", "Other entities attack range", 4.0, 3.0, 10.0, 0.05, NumberProperty.FORMAT_DISTANCE);
    private final NumberProperty rotRange = new NumberProperty("Rotate", "Rotation range", 5.2, 3.0, 16.0, 0.05, NumberProperty.FORMAT_DISTANCE);
    private final NumberProperty swingRange = new NumberProperty("Swing", "Swing range", 5.2, 3.0, 16.0, 0.05, NumberProperty.FORMAT_DISTANCE);
    private final NumberProperty fov = new NumberProperty("FOV", "Targets must be in FOV.", 360.0, 0.0, 360.0, 1.0, NumberProperty.FORMAT_ANGLE);

    private final GroupProperty rotsGroup = new GroupProperty("Rotations", "Kill Aura Rotations");
    private final EnumProperty<HitVecMode> hitVecMode = new EnumProperty<>("Hit Vector", "Hit vector of the entity", HitVecMode.CLOSEST);

    private final EnumProperty<Swing> swingMode = new EnumProperty<>("Swing", "Swinging", Swing.CLIENT);
    private final BooleanProperty autoDisable = new BooleanProperty("Auto Disable", "Automatically disabled Kill Aura in certain situations", true);
    private final NumberProperty switchDelay = new NumberProperty("Switch Delay", "Delay between switching targets", () -> this.mode.is(Mode.SWITCH), 250.0, 0.0, 1000.0, 50.0, NumberProperty.FORMAT_MS);
    private final NumberProperty maxTargets = new NumberProperty("Max Targets", "Maximum amount of targets", () -> this.mode.is(Mode.SWITCH), 15, 1, 50, 1, NumberProperty.FORMAT_INT);

    private final List<EntityLivingBase> targets = new ArrayList<>();
    private final List<EntityLivingBase> invalidTargets = new ArrayList<>();
    private EntityLivingBase target = null;
    private boolean isTargetValid = false;

    private int targetIndex = 0;

    private int nextDelay = -1;

    private final Clock attackTimer = new Clock();
    private final Clock switchTimer = new Clock();

    public KillAura() {
        this.targetsGroup.addProperties(this.players, this.mobs, this.animals, this.others, this.invis, this.dead, this.teams, this.existed);
        this.rangeGroup.addProperties(this.playerRange, this.otherRange, this.rotRange, this.swingRange, this.fov);
        this.rotsGroup.addProperties(this.hitVecMode);
        this.addProperties(this.mode, this.sorting, this.minAps, this.maxAps, this.targetsGroup, this.rangeGroup, this.rotsGroup, this.swingMode, this.autoDisable, this.switchDelay, this.maxTargets);
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

    @Override
    public void onEnable() {
        this.cleanup();
    }

    @Override
    public void onDisable() {
        this.cleanup();
    }

    @EventLink
    public final Listener<EventTick> onTick = event -> {

        if (!PlayerUtil.canUpdate()) {
            return;
        }

        if (this.autoDisable.get() && mc.thePlayer.isDead) {
            this.toggle(false);
            Notification.send("Death", "Kill Aura was automatically disabled", NotificationType.WARNING, 3000);
            return;
        }

        if (this.nextDelay == -1) {
            this.nextDelay = (int)(1000.0F / (float)ThreadLocalRandom.current().nextInt(this.minAps.getInt(), this.maxAps.getInt() + 1));
        }

        this.updateTargetList();

        if (this.targetIndex >= Math.min(this.targets.size(), this.maxTargets.getInt())) {
            this.targetIndex = 0;
        }

        if (!this.targets.isEmpty()) {
            if (this.mode.is(Mode.SWITCH)) {
                if (this.switchTimer.hasReached(this.switchDelay.getInt())) {
                    this.incrementTargetIndex();
                    this.switchTimer.reset();
                }
            } else {
                this.targetIndex = 0;
            }
            this.sortTargets(this.targets);
            this.target = this.targets.get(this.targetIndex);
            this.isTargetValid = true;
        } else if (!this.invalidTargets.isEmpty()) {
            this.target = this.invalidTargets.get(0);
            this.isTargetValid = false;
        } else {
            this.target = null;
        }

    };

    @EventLink
    public final Listener<EventUpdate> onUpdate = event -> {
        if (this.target == null) {
            return;
        }

        Vec3 hitVec = this.getHitVec(this.target);

        boolean canSwing = hitVec.distanceTo(PlayerUtil.eyesPos()) <= this.swingRange.getDouble();

        if (this.isTargetValid && this.target.hurtResistantTime <= 15) {
            PacketUtil.leftClickPackets(new MovingObjectPosition(this.target, hitVec), switch (this.swingMode.get()) {
                case CLIENT, ATTACK_ONLY -> true;
                case SILENT -> false;
            });
        } else if (canSwing) {
            PacketUtil.leftClickPackets(new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, hitVec, null, new BlockPos(hitVec)), switch (this.swingMode.get()) {
                case CLIENT -> true;
                case SILENT, ATTACK_ONLY -> false;
            });
        }

        this.attackTimer.reset();
        this.nextDelay = 20 / (int) ((Math.random() * (this.maxAps.getDouble() - this.minAps.getDouble())) + this.minAps.getDouble());

    };

    @EventLink
    public final Listener<EventPreMotion> onPre = event -> {
        if (this.target == null) {
            return;
        }

        Vec3 hitVec = this.getHitVec(this.target);
        if (hitVec.distanceTo(PlayerUtil.eyesPos()) <= this.rotRange.getDouble()) {
            Rotation rotation = RotationUtil.getRotations(hitVec);
            event.setRotations(rotation);
        }


    };

    private Vec3 getHitVec(EntityLivingBase entity) {
        return switch (this.hitVecMode.get()) {
            case CLOSEST -> MathUtil.closestPoint(entity.getEntityBoundingBox(), PlayerUtil.eyesPos());
            case HEAD -> new Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        };
    }

    private void incrementTargetIndex() {
        this.targetIndex++;
        if (this.targetIndex >= Math.min(this.targets.size(), this.maxTargets.getInt())) {
            this.targetIndex = 0;
        }
    }

    private void updateTargetList() {
        this.targets.clear();
        this.invalidTargets.clear();
        this.targets.addAll(mc.theWorld.getEntities(EntityLivingBase.class, entity -> this.isTargetValid(entity) == TargetValidity.ATTACK));
        this.invalidTargets.addAll(mc.theWorld.getEntities(EntityLivingBase.class, entity -> this.isTargetValid(entity) == TargetValidity.TARGET));
    }

    private void sortTargets(List<EntityLivingBase> targets) {
        targets.sort(switch (this.sorting.get()) {
            case ANGLE -> Comparator.comparingDouble(entity -> RotationUtil.getRotations(entity).yaw);
            case RANGE -> Comparator.comparingDouble(entity -> entity.getDistanceToEntity(mc.thePlayer));
            case FOV -> Comparator.comparingDouble(entity -> Math.abs(RotationUtil.getYawChange(entity.posX, entity.posZ)));
            case ARMOR -> Comparator.comparingInt(entity -> entity instanceof EntityPlayer ? ((EntityPlayer) entity).inventory.getTotalArmorValue() : (int) entity.getHealth());
            case HEALTH -> Comparator.comparingDouble(EntityLivingBase::getHealth);
        });
    }

    private TargetValidity isTargetValid(EntityLivingBase target) {
        if (target == null || target.isClientPlayer()) {
            return TargetValidity.INVALID;
        }

        if ((target.getHealth() <= 0.0F || target.isDead) && !this.dead.get()) {
            return TargetValidity.INVALID;
        }

        if (target.ticksExisted < this.existed.getInt()) {
            return TargetValidity.INVALID;
        }

        if (Nonsense.module(AntiBot.class).isBot(target)) {
            return TargetValidity.INVALID;
        }

        double attackRange = target instanceof EntityPlayer ? playerRange.getDouble() : otherRange.getDouble();
        double maxRange = Math.max(attackRange, Math.max(rotRange.getDouble(), swingRange.getDouble()));

        Vec3 hitVec = this.getHitVec(target);
        double dist = hitVec.distanceTo(PlayerUtil.eyesPos());

        if (dist > maxRange) {
            return TargetValidity.INVALID;
        }
        TargetValidity result = dist <= attackRange ? TargetValidity.ATTACK : TargetValidity.TARGET;

        if (!this.isInFov(target)) {
            return TargetValidity.INVALID;
        }

        if (target instanceof EntityPlayer player) {

            if (!this.players.get()) {
                return TargetValidity.INVALID;
            }

            if (player.isPlayerSleeping()) {
                return TargetValidity.INVALID;
            }

            if (this.teams.get() && PlayerUtil.isOnSameTeam(player)) {
                return TargetValidity.INVALID;
            }

            if (!this.invis.get() && target.isInvisible()) {
                return TargetValidity.INVALID;
            }

            return result;
        }

        if (target instanceof EntityMob || this.target instanceof EntitySlime) {
            return this.mobs.get() ? result : TargetValidity.INVALID;
        } else if (target instanceof EntityAnimal) {
            return this.animals.get() ? result : TargetValidity.INVALID;
        } else {
            return this.others.get() ? result : TargetValidity.INVALID;
        }

    }

    private boolean isInFov(EntityLivingBase entity) {
        float fov = this.fov.getFloat();
        return RotationUtil.getYawChange(entity.posX, entity.posZ) <= fov && RotationUtil.getPitchChange(entity, entity.posY) <= fov;
    }

    private void cleanup() {
        this.targets.clear();
        this.invalidTargets.clear();
        this.target = null;
        this.nextDelay = -1;
    }

    private enum Mode {
        SINGLE,
        SWITCH
    }

    private enum HitVecMode {
        CLOSEST,
        HEAD
    }

    private enum Sorting {
        ANGLE,
        RANGE,
        @DisplayName("FOV") FOV,
        ARMOR,
        HEALTH,
    }

    private enum Swing {
        CLIENT,
        SILENT,
        ATTACK_ONLY
    }

    private enum TargetValidity {
        ATTACK,
        TARGET,
        INVALID
    }

}
