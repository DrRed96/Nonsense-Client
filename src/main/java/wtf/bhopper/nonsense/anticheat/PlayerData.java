package wtf.bhopper.nonsense.anticheat;

import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.anticheat.check.Check;
import wtf.bhopper.nonsense.anticheat.check.data.ICheckData;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.util.minecraft.IMinecraft;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.world.BlockUtil;
import wtf.bhopper.nonsense.util.misc.Clock;
import wtf.bhopper.nonsense.util.misc.EvictingArrayList;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;

import java.util.*;
import java.util.function.Supplier;

public class PlayerData implements IMinecraft {

    private final Map<Check, Float> violations = new HashMap<>();
    private EntityPlayer entity;

    private double posX;
    private double posY;
    private double posZ;
    private double prevPosX;
    private double prevPosY;
    private double prevPosZ;

    private double deltaX;
    private double deltaY;
    private double deltaZ;
    private double deltaXZ;
    private double prevDeltaX;
    private double prevDeltaY;
    private double prevDeltaZ;
    private double prevDeltaXZ;
    private double prevPrevDeltaXZ;

    private float rotationYaw;
    private float rotationPitch;
    private float prevRotationYaw;
    private float prevRotationPitch;

    private float deltaYaw;
    private float deltaPitch;
    private float prevDeltaYaw;
    private float prevDeltaPitch;
    private float totalDeltaRotations;

    private boolean onGround;
    private boolean prevOnGround;
    private int airTicks, teleportTicks;

    private boolean groundCollision, prevGroundCollision;

    private boolean sneaking;
    private boolean sprinting;
    private boolean usingItem;
    private int blocks;
    private Clock useClock;

    private final List<Long> updateDelayList = new EvictingArrayList<>(40);
    private final List<Long> closeUpdateDelayList = new EvictingArrayList<>(2);
    private final Clock updateClock = new Clock();
    private final Clock lastGroundCollision = new Clock();
    private long lastUpdateTime;

    private boolean hacking;
    private final Clock lastSwing = new Clock();
    private final Clock lastVelocity = new Clock();
    private final Clock firstSeen = new Clock();

    private Item recentItem;

    private final Map<Class<? extends ICheckData>, ICheckData> checkData = new HashMap<>();

    public PlayerData(EntityPlayer entity) {
        this.entity = entity;
        this.updatePosition(entity.prevPosX, entity.prevPosY, entity.prevPosZ);
        this.updatePosition(entity.posX, entity.posY, entity.posZ);
        this.updateRotation(entity.prevRotationYaw, entity.prevRotationPitch);
        this.updateRotation(entity.rotationYaw, entity.rotationPitch);
        this.prevOnGround = this.onGround = entity.onGround;
        if (entity.getHeldItem() != null) {
            this.recentItem = entity.getHeldItem().getItem();
        }
    }

    public Clock getFirstSeen() {
        return firstSeen;
    }

    public Clock getLastVelocity() {
        return lastVelocity;
    }

    private void updateDelay() {
        if (!this.updateClock.hasReached(3000L)) {
            this.updateDelayList.add(this.updateClock.getTime());
            this.closeUpdateDelayList.add(this.updateClock.getTime());
        }
        this.lastUpdateTime = this.updateClock.getTime();
        this.updateClock.reset();
    }

    public float getMoveYaw(double x, double z) {
        double moveYaw = Math.atan2(-x, z) * (180 / Math.PI);
        if (moveYaw < 0) {
            moveYaw += 360;
        }
        return (float) moveYaw;
    }

    public float getMoveYaw() {
        return getMoveYaw(getDeltaX(), getDeltaZ());
    }

    public void handleRelMove(S14PacketEntity packetEntity) {
        this.updateDelay();

        double x = (double) (this.entity.serverPosX + packetEntity.getX()) / 32.0;
        double y = (double) (this.entity.serverPosY + packetEntity.getY()) / 32.0;
        double z = (double) (this.entity.serverPosZ + packetEntity.getZ()) / 32.0;
        float yaw = packetEntity.isRotating() ? (float) (packetEntity.getYaw() * 360) / 256.0F : this.rotationYaw;
        float pitch = packetEntity.isRotating() ? (float) (packetEntity.getPitch() * 360) / 256.0F : this.rotationPitch;

        this.updatePosition(x, y, z);
        this.updateRotation(yaw, pitch);

        this.prevOnGround = this.onGround;
        this.onGround = packetEntity.isOnGround();

        if (!this.onGround) {
            if (prevOnGround) {
                this.lastGroundCollision.reset();
            }
            this.airTicks++;
        } else {
            this.airTicks = 0;
        }

        this.teleportTicks++;
    }

    public void handleTeleport(S18PacketEntityTeleport packetEntityTeleport) {
        this.updateDelay();

        double x = (double) packetEntityTeleport.getX() / 32.0;
        double y = (double) packetEntityTeleport.getY() / 32.0;
        double z = (double) packetEntityTeleport.getZ() / 32.0;
        float yaw = (float) (packetEntityTeleport.getYaw() * 360) / 256.0F;
        float pitch = (float) (packetEntityTeleport.getPitch() * 360) / 256.0F;

        this.updatePosition(x, y, z);
        this.updateRotation(yaw, pitch);

        this.prevOnGround = this.onGround;
        this.onGround = packetEntityTeleport.getOnGround();

        if (!this.onGround) {
            this.airTicks++;
        } else {
            this.airTicks = 0;
        }

        this.teleportTicks = 0;
    }

    public void handleAnimation(S0BPacketAnimation packetAnimation) {
        if (packetAnimation.getAnimationType() == 0) {
            this.lastSwing.reset();
        }
    }

    public void handleEquipment(S04PacketEntityEquipment equipment) {
        if (equipment.getEquipmentSlot() == 0 && equipment.getItemStack() != null) {
            this.recentItem = equipment.getItemStack().getItem();
        }
    }

    public void handleHeadLook(S19PacketEntityHeadLook packetEntityHeadLook) {
        this.updateRotation((float) (packetEntityHeadLook.getYaw() * 360) / 256.0F, null);
    }

    public void handleEntityMetadata(S1CPacketEntityMetadata packetEntityMetadata) {
        if (packetEntityMetadata.getMetadata() != null) {
            for (DataWatcher.WatchableObject object : packetEntityMetadata.getMetadata()) {
                if (object.getDataValueId() == 0 && object.getObject() instanceof Byte) {
                    byte flagValue = (byte) object.getObject();
                    boolean sneaking = (flagValue & (1 << 1)) != 0;
                    boolean sprinting = (flagValue & (1 << 3)) != 0;
                    boolean using = (flagValue & (1 << 4)) != 0;
                    if (using) {
                        if (!this.usingItem) {
                            this.useClock = new Clock();
                            this.usingItem = true;
                        }
                        if (sneaking == this.sneaking && sprinting == this.sprinting) {
                            this.blocks++;
                        }
                    } else {
                        this.useClock = null;
                        this.usingItem = false;
                        this.blocks = 0;
                    }
                    this.sneaking = sneaking;
                    this.sprinting = sprinting;
                }
            }
        }
    }

    private void updatePosition(double posX, double posY, double posZ) {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;

        this.prevDeltaX = this.deltaX;
        this.prevDeltaY = this.deltaY;
        this.prevDeltaZ = this.deltaZ;
        this.prevPrevDeltaXZ = this.prevDeltaXZ;
        this.prevDeltaXZ = this.deltaXZ;
        this.deltaX = posX - this.prevPosX;
        this.deltaY = posY - this.prevPosY;
        this.deltaZ = posZ - this.prevPosZ;
        this.deltaXZ = Math.hypot(this.deltaX, this.deltaZ);

        if (mc.theWorld == null) {
            return;
        }

        this.prevGroundCollision = this.groundCollision;
        this.groundCollision = BlockUtil.hasAnyCollisionsUnder(PlayerUtil.getPlayerBoundingBoxForPosition(new Vec3(posX, posY, posZ)).expand(0.15D, 0.0125D, 0.15D), posY);
        if (this.prevGroundCollision && !this.groundCollision) {
            this.lastGroundCollision.reset();
        }
    }

    private void updateRotation(float rotationYaw, Float rotationPitch) {
        this.prevRotationYaw = this.rotationYaw;
        this.rotationYaw = rotationYaw;

        this.prevDeltaYaw = this.deltaYaw;
        this.deltaYaw = rotationYaw - this.prevRotationYaw;

        if (rotationPitch != null) {
            this.prevRotationPitch = this.rotationPitch;
            this.rotationPitch = rotationPitch;

            this.prevDeltaPitch = this.deltaPitch;
            this.deltaPitch = rotationPitch - this.prevRotationPitch;
        }

        final float deltaYaw180 = MathHelper.wrapAngleTo180_float(this.deltaYaw);
        this.totalDeltaRotations = (float) Math.sqrt(deltaYaw180 * deltaYaw180 + deltaPitch * deltaPitch);
    }

    public float incrementVl(Check check) {
        return incrementVl(check, 1);
    }

    public float incrementVl(Check check, int amount) {
        float vl = this.violations.getOrDefault(check, 0.0F) + amount;

        boolean flagged = vl >= check.maxVl;

        ChatUtil.Builder.of("\247d\247cAnti-Cheat %s%s \247rhas failed check \2476%s \2477\247ox%,d",
                        this.hacking || flagged ? EnumChatFormatting.RED : EnumChatFormatting.WHITE, this.entity.getName(), check.name, (int)vl)
                .setColor(EnumChatFormatting.GRAY)
                .setHoverEvent(GeneralUtil.paragraph(
                        "\247c\247l" + check.name,
                        "\2477" + check.description
                ))
                .send(-Math.abs(entity.getEntityId()));

        if (!this.hacking && flagged) {
            this.hacking = true;
            Notification.send("Anti Cheat", entity.getName() + " is cheating.", NotificationType.WARNING, 5000);
        }


        this.violations.put(check, vl);
        return vl;
    }

    public float decrementVl(Check check, float multiplier) {
        if (this.violations.containsKey(check)) {
            float newVl = this.violations.get(check) * multiplier;
            if (newVl < 0) {
                newVl = 0;
            }
            if (!this.isHacking()) {
                this.violations.put(check, newVl);
            }
            return newVl;
        }
        return 0.0F;
    }

    public EntityPlayer getEntity() {
        return this.entity;
    }

    public void setEntity(EntityPlayer entity) {
        this.entity = entity;
    }

    public double getPosX() {
        return this.posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public double getPosZ() {
        return this.posZ;
    }

    public double getPrevPosX() {
        return this.prevPosX;
    }

    public double getPrevPosY() {
        return this.prevPosY;
    }

    public double getPrevPosZ() {
        return this.prevPosZ;
    }

    public double getDeltaX() {
        return this.deltaX;
    }

    public double getDeltaY() {
        return this.deltaY;
    }

    public double getDeltaZ() {
        return this.deltaZ;
    }

    public double getDeltaXZ() {
        return this.deltaXZ;
    }

    public double getPrevDeltaX() {
        return this.prevDeltaX;
    }

    public double getPrevDeltaY() {
        return this.prevDeltaY;
    }

    public double getPrevDeltaZ() {
        return this.prevDeltaZ;
    }

    public float getTotalDeltaRotations() {
        return this.totalDeltaRotations;
    }

    public double getPrevDeltaXZ() {
        return this.prevDeltaXZ;
    }

    public double getPrevPrevDeltaXZ() {
        return this.prevPrevDeltaXZ;
    }

    public float getRotationYaw() {
        return this.rotationYaw;
    }

    public float getRotationPitch() {
        return this.rotationPitch;
    }

    public float getPrevRotationYaw() {
        return this.prevRotationYaw;
    }

    public float getPrevRotationPitch() {
        return this.prevRotationPitch;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public boolean isPrevOnGround() {
        return this.prevOnGround;
    }

    public int getAirTicks() {
        return this.airTicks;
    }

    public boolean isGroundCollision() {
        return this.groundCollision;
    }

    public boolean isPrevGroundCollision() {
        return this.prevGroundCollision;
    }

    public boolean isSneaking() {
        return this.sneaking;
    }

    public boolean isSprinting() {
        return this.sprinting;
    }

    public boolean isUsingItem() {
        return this.usingItem;
    }

    public int getBlocks() {
        return this.blocks;
    }

    public Clock getUseClock() {
        return this.useClock;
    }

    public float getDeltaYaw() {
        return this.deltaYaw;
    }

    public float getDeltaPitch() {
        return this.deltaPitch;
    }

    public float getPrevDeltaYaw() {
        return this.prevDeltaYaw;
    }

    public float getPrevDeltaPitch() {
        return this.prevDeltaPitch;
    }

    public int getTeleportTicks() {
        return this.teleportTicks;
    }

    public Double getAverageUpdateLag() {
        OptionalDouble optionalAverage = this.updateDelayList.stream().mapToLong(a -> a).average();
        if (optionalAverage.isEmpty()) {
            return null;
        }
        return optionalAverage.getAsDouble();
    }

    public Double getCloseAverageUpdateLag() {
        OptionalDouble optionalAverage = this.closeUpdateDelayList.stream().mapToLong(a -> a).average();
        if (optionalAverage.isEmpty()) {
            return null;
        }
        return optionalAverage.getAsDouble();
    }

    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public Clock getUpdateClock() {
        return this.updateClock;
    }

    public boolean isHacking() {
        return this.hacking;
    }

    public Item getRecentItem() {
        return this.recentItem;
    }

    public Clock getLastSwing() {
        return this.lastSwing;
    }

    @SuppressWarnings("unchecked")
    public <T extends ICheckData> T getCheckData(Class<T> clazz, Supplier<T> supplier) {
        return (T)this.checkData.computeIfAbsent(clazz, _ -> Objects.requireNonNull(supplier.get()));
    }

    @SuppressWarnings("unchecked")
    public <T extends ICheckData> T getCheckData(Class<T> clazz) {
        return this.getCheckData(clazz, null);
    }

    public boolean hasCheckData(Class<? extends ICheckData> dataClazz) {
        return this.checkData.containsKey(dataClazz);
    }

    public Map<Check, Float> getViolations() {
        return this.violations;
    }

}
