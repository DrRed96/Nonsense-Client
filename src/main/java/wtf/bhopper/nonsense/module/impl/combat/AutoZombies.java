package wtf.bhopper.nonsense.module.impl.combat;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventPreMotion;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.player.Rotation;
import wtf.bhopper.nonsense.util.minecraft.player.RotationUtil;
import wtf.bhopper.nonsense.util.minecraft.world.BlockUtil;

@ModuleInfo(name = "Zombies Aim Bot",
        description = "Some tools for zombies, made by Ciyuna",
        category = ModuleCategory.COMBAT)
public class AutoZombies extends Module {

    private final BooleanProperty silent = new BooleanProperty("Silent Aim", "Aims silently", true);
    private final BooleanProperty wallShooting = new BooleanProperty("Wall Aiming", "Aims through walls", false);

    private final GroupProperty mobSelectorGroup = new GroupProperty("Mob Selector", "Only shoot specific mobs", this);
    private final BooleanProperty mobSelector = new BooleanProperty("Enable", "Enables the mob selector", false);
    private final BooleanProperty zombies = new BooleanProperty("Zombies", "Target zombies", true);
    private final BooleanProperty wolves = new BooleanProperty("Wolves", "Target wolves", false);
    private final BooleanProperty magmaCubes = new BooleanProperty("Magma Cubes", "Target magma cubes", false);
    private final BooleanProperty skeletons = new BooleanProperty("Skeletons", "Target skeletons", false);
    private final BooleanProperty blazes = new BooleanProperty("Blazes", "Target blazes", false);
    private final BooleanProperty endermites = new BooleanProperty("Entermites", "Target endermites", false);

    private final GroupProperty predictionGroup = new GroupProperty("Prediction", "Predicts where a zombie will be before shooting", this);
    private final BooleanProperty prediction = new BooleanProperty("Enable", "Enables prediction", true);
    private final NumberProperty predictionStrength = new NumberProperty("Strength", "Prediction strength", 1.0, 0.1, 2.0, 0.1);
    private final NumberProperty minPredictionDistance = new NumberProperty("Min Distance", "Minimum distance to use prediction", 2.0F, 1.0F, 20.0F, 0.5F);
    private final NumberProperty maxPredictionDistance = new NumberProperty("Max Distance", "Maximum distance to use prediction", 12.0F, 1.0F, 20.0F, 0.5F);
    private final BooleanProperty adaptivePrediction = new BooleanProperty("Adaptive", "Uses adaptive prediction", true);
    private final BooleanProperty heightPrediction = new BooleanProperty("Height", "Accounts for enemy height", true);
    private final NumberProperty verticalPrediction = new NumberProperty("Vertical", "Vertical prediction", 1.0, 0.1, 2.0, 0.1);

    private int validTargets = 0;
    private boolean shouldShoot = false;

    public AutoZombies() {
        this.predictionGroup.addProperties(this.prediction,
                this.predictionStrength,
                this.minPredictionDistance,
                this.maxPredictionDistance,
                this.adaptivePrediction,
                this.heightPrediction,
                this.verticalPrediction);
        this.mobSelectorGroup.addProperties(this.mobSelector, this.endermites, this.wolves, this.magmaCubes, this.skeletons, this.blazes, this.endermites);
        this.addProperties(this.silent, this.wallShooting, this.mobSelectorGroup, this.predictionGroup);

        this.minPredictionDistance.addValueChangeListener((oldValue, value) -> {
            if (this.maxPredictionDistance.getDouble() < value) {
                this.maxPredictionDistance.set(value);
            }
        });

        this.maxPredictionDistance.addValueChangeListener((oldValue, value) -> {
            if (this.minPredictionDistance.getDouble() > value) {
                this.minPredictionDistance.set(value);
            }
        });
    }

    @EventLink
    public final Listener<EventPreMotion> onPre = event -> {
        this.validTargets = 0;
        this.shouldShoot = false;

        EntityLivingBase target = this.getBestTarget();

        if (target == null) {
            return;
        }

        this.shouldShoot = true;

        double x = target.posX;
        double y = target.posY + target.getEyeHeight() + 0.1;
        double z = target.posZ;

        if (this.prediction.get()) {
            double distance = Math.sqrt(x * x + z * z);
            double scale = this.adaptivePrediction.get()
                    ? MathHelper.clamp_double((distance - this.minPredictionDistance.getDouble()) / (this.maxPredictionDistance.getDouble() - this.minPredictionDistance.getDouble()), 0.0, 1.0)
                    : 1.0F;

            double motionStrength = this.predictionStrength.getDouble() * scale;

            x += target.motionX * motionStrength;
            z += target.motionZ * motionStrength;

            if (this.heightPrediction.get()) {
                y += target.motionY * verticalPrediction.getDouble();
            }

        }

        Rotation rotation = RotationUtil.getRotations(x, y, z);
        if (this.silent.get()) {
            event.setRotations(rotation);
        } else {
            mc.thePlayer.rotationYaw = rotation.yaw;
            mc.thePlayer.rotationPitch = rotation.pitch;
        }


    };

    private EntityLivingBase getBestTarget() {
        EntityLivingBase bestTarget = null;
        double closestDistance = Double.MAX_VALUE;
        boolean bestTargetVisible = false;
        float bestTargetHealth = Float.MAX_VALUE;

        Vec3 eyePos = PlayerUtil.eyesPos();

        for (EntityLivingBase entity : mc.theWorld.getEntities(EntityLivingBase.class, this::isValidTarget)) {
            this.validTargets++;

            Vec3 targetPos = entity.getPositionEyes(1.0F);
            MovingObjectPosition mop = mc.theWorld.rayTraceBlocks(eyePos, targetPos, false, true, false);

            boolean isVisible = true;

            if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                if (!this.wallShooting.get()) {
                    continue;
                }

                Block block = BlockUtil.getBlock(mop.getBlockPos());

                if (!switch (block) {
                    case BlockStairs stairs -> stairs != Blocks.dark_oak_stairs && stairs != Blocks.spruce_stairs;
                    case BlockSlab slab -> slab.getMaterial() != Material.rock;
                    case BlockFence _, BlockChest _, BlockSign _ -> true;
                    default -> block == Blocks.iron_bars;
                }) {
                    continue;
                }

                isVisible = false;

            }

            double deltaX = entity.posX - mc.thePlayer.posX;
            double deltaZ = entity.posZ - mc.thePlayer.posZ;

            double distanceSq = deltaX * deltaX + deltaZ * deltaZ;

            if (bestTarget == null || isVisible && !bestTargetVisible || isVisible == bestTargetVisible && entity.getHealth() < bestTargetHealth || isVisible == bestTargetVisible && Math.abs(entity.getHealth() - bestTargetHealth) < 2.0F && distanceSq < closestDistance) {
                bestTarget = entity;
                closestDistance = distanceSq;
                bestTargetVisible = isVisible;
                bestTargetHealth = entity.getHealth();
            }

        }

        return bestTarget;
    }

    private boolean isValidTarget(EntityLivingBase entity) {

        if (entity == null) {
            return false;
        }

        if (entity.isInvisible() || entity.isDead || entity.getHealth() <= 0.0F) {
            return false;
        }

        if (!this.mobSelector.get()) {
            return true;
        }

        return switch (entity) {
            case EntityZombie _ -> this.zombies.get();
            case EntityWolf _ -> this.wolves.get();
            case EntityMagmaCube _ -> this.magmaCubes.get();
            case EntitySkeleton _ -> this.skeletons.get();
            case EntityBlaze _ -> this.blazes.get();
            case EntityEndermite _ -> this.endermites.get();
            default -> false;
        };

    }

}
