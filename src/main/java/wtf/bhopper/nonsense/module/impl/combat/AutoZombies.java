package wtf.bhopper.nonsense.module.impl.combat;

import net.minecraft.block.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.component.impl.player.RotationsComponent;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventSelectItem;
import wtf.bhopper.nonsense.event.impl.player.EventUpdate;
import wtf.bhopper.nonsense.event.impl.player.interact.EventClickAction;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.player.Rotation;
import wtf.bhopper.nonsense.util.minecraft.player.RotationUtil;
import wtf.bhopper.nonsense.util.minecraft.world.BlockUtil;

import java.util.Comparator;

@ModuleInfo(name = "Auto Zombies",
        description = "Some tools for zombies, made by Ciyuna",
        category = ModuleCategory.COMBAT)
public class AutoZombies extends Module {

    private final BooleanProperty silent = new BooleanProperty("Silent Aim", "Aims silently", true);
    private final BooleanProperty autoShoot = new BooleanProperty("Auto Shoot", "Automatically shoots", true);
    private final BooleanProperty autoSwap = new BooleanProperty("Auto Swap", "Automatically swaps weapons", true);

    private EntityLivingBase target = null;

    public AutoZombies() {
        this.addProperties(this.silent, this.autoShoot, this.autoSwap);
    }

    @EventLink
    public final Listener<EventUpdate> onUpdate = _ -> {

        this.target = this.getTarget();

        if (this.target == null) {
            return;
        }

        if (this.target.getEntityBoundingBox().isVecInside(PlayerUtil.eyesPos())) {
            return;
        }

        double height = this.target.getEntityBoundingBox().maxY - this.target.getEntityBoundingBox().minY;
        Vec3 targetPos = this.target.getPositionVector().addVector(0.0, height * 0.7, 0.0);

        double distance = mc.thePlayer.getDistanceToEntity(this.target);
        if (distance > 15.0) {
            double prediction = Math.min(distance * 0.1, 2.0);

            targetPos = targetPos.addVector(
                    target.motionX * prediction,
                    target.motionY * prediction,
                    target.motionZ * prediction
            );
        }

        Rotation rotation = RotationUtil.getRotations(targetPos);
        RotationsComponent.updateServerRotations(rotation);

        if (!this.silent.get()) {
            mc.thePlayer.rotationYaw = rotation.yaw;
            mc.thePlayer.rotationPitch = rotation.pitch;
        }

    };

    @EventLink
    public final Listener<EventSelectItem> onSelect = event -> {
        if (this.autoSwap.get()) {
            int lastSlot = event.slot;
            do {
                event.slot = ((event.slot + 1) % 9);
                if (event.slot == lastSlot) {
                    break;
                }
            } while (!this.isHoldingGunInSlot(event.slot));
        }
    };

    @EventLink
    public final Listener<EventClickAction> onClick = event -> {
        if (this.autoShoot.get()) {
            if (!event.usingItem && this.target != null && this.isGun(mc.thePlayer.getHeldItem()) && target.hurtResistantTime < 15) {
                event.right = true;
            }
        }
    };

    public boolean isGun(ItemStack stack) {
        if (stack == null) {
            return false;
        }

        Item item = stack.getItem();
        return (stack.getItemDamage() == 0 || stack.getItemDamage() == stack.getMaxDamage() - 1 || stack.stackSize > 1) &&
                stack.hasDisplayName() && stack.getDisplayName().startsWith("\2476") &&
                (item instanceof ItemHoe ||
                        item instanceof ItemSpade ||
                        item instanceof ItemPickaxe ||
                        item instanceof ItemFlintAndSteel ||
                        item instanceof ItemShears);
    }

    public boolean isHoldingGunInSlot(int slot) {
        return this.isGun(mc.thePlayer.inventory.mainInventory[slot]);
    }

    public boolean canTarget(EntityLivingBase entity) {

        if (entity.isDead || entity.getHealth() <= 0.0F) {
            return false;
        }

        Vec3 start = PlayerUtil.eyesPos();
        Vec3 end = entity.getPositionVector().addVector(0.0, entity.getEyeHeight(), 0.0);
        MovingObjectPosition intercept = mc.theWorld.rayTraceBlocks(start, end, false, true, false);

        if (intercept == null || intercept.entityHit == entity) {
            return true;
        }

        Block block = BlockUtil.getBlock(intercept.getBlockPos());

        return (block instanceof BlockStairs && !(block.getUnlocalizedName().contains("darkOak") || block.getUnlocalizedName().contains("spruce"))) ||
                block instanceof BlockSlab ||
                block instanceof BlockBarrier ||
                block instanceof BlockFence ||
                block == Blocks.iron_bars ||
                block instanceof BlockChest ||
                block instanceof BlockSign;
    }

    public EntityLivingBase getTarget() {
        return mc.theWorld.loadedEntityList.stream()
                .filter(entity ->
                        entity instanceof EntityZombie ||
                        entity instanceof EntitySkeleton ||
                        entity instanceof EntityBlaze ||
                        entity instanceof EntityWolf ||
                        entity instanceof EntityMagmaCube ||
                        entity instanceof EntityEndermite ||
                        entity instanceof EntitySilverfish)
                .map(e -> (EntityLivingBase)e)
                .filter(this::canTarget)
                .min(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity)))
                .orElse(null);
    }


}
