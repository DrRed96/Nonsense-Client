package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.component.impl.player.RotationsComponent;
import wtf.bhopper.nonsense.event.EventPriorities;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.event.impl.player.EventUpdate;
import wtf.bhopper.nonsense.event.impl.player.interact.EventClickAction;
import wtf.bhopper.nonsense.event.impl.player.inventory.EventChangeItem;
import wtf.bhopper.nonsense.event.impl.player.inventory.EventSelectItem;
import wtf.bhopper.nonsense.event.impl.player.movement.EventMovementInput;
import wtf.bhopper.nonsense.event.impl.render.*;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.inventory.ItemBuilder;
import wtf.bhopper.nonsense.util.minecraft.player.*;

@ModuleInfo(name = "Freecam",
        description = "Silent spectator mode",
        category = ModuleCategory.VISUAL)
public class Freecam extends Module {

    private final NumberProperty speed = new NumberProperty("Speed", "Freecam fly speed", 4.0, 1.0, 10.0, 1.0);
    private final BooleanProperty autoRefresh = new BooleanProperty("Auto Refresh", "Automatically refresh chunks when Free Camera is disabled", false);

    private Vec3 position = null;
    private Vec3 prevPosition = null;
    private Rotation serverRotation = null;

    private EntityOtherPlayerMP clientEntity = null;
    private EntityOtherPlayerMP silentEntity = null;

    private State state = State.NORMAL;
    private final ItemStack[] mainInventory = new ItemStack[36];
    private final ItemStack[] speedInventory = new ItemStack[36];
    private final ItemStack[] nukerInventory = new ItemStack[36];
    private int slot = 0;

    public Freecam() {
        this.addProperties(this.speed, this.autoRefresh);
        this.setHidden(false);

        mainInventory[0] = ItemBuilder.of(Items.sugar)
                .setDisplayName("\247bSpeed")
                .build();

        mainInventory[1] = ItemBuilder.of(Items.blaze_rod)
                .setDisplayName("\2475Magic Wand")
                .build();

        mainInventory[7] = ItemBuilder.of(Blocks.tnt)
                .setDisplayName("\247cNuker Mode")
                .build();

        mainInventory[8] = ItemBuilder.of(Items.feather)
                .setDisplayName("\2477Back")
                .build();

        for (int i = 0; i < 9; i++) {
            speedInventory[i] = ItemBuilder.of(Items.sugar)
                    .setAmount(i + 1)
                    .setDisplayName("\247bSpeed \2477" + (i + 1))
                    .build();
        }

        nukerInventory[6] = ItemBuilder.of(Items.diamond_pickaxe)
                .setDisplayName("\247cNuke")
                .setLore("Use this pickaxe to break large amounts of blocks.")
                .setFakeEnchanted()
                .addTag("Nonsense", new NBTTagByte((byte)0))
                .build();

        nukerInventory[7] = ItemBuilder.of(Items.stick)
                .setDisplayName("\2476Teleport")
                .setLore("Use this stick to set your server-side position.")
                .setFakeEnchanted()
                .addTag("Nonsense", new NBTTagByte((byte)1))
                .build();

        nukerInventory[8] = ItemBuilder.of(Items.feather)
                .setDisplayName("\2477Back")
                .addTag("Nonsense", new NBTTagByte((byte)2))
                .build();
    }

    @Override
    public void onEnable() {
        this.serverRotation = null;
        this.position = null;
        this.clientEntity = null;
        this.silentEntity = null;
        this.state = State.NORMAL;
    }

    @Override
    public void onDisable() {
        mc.setRenderViewEntity(mc.thePlayer);
        if (this.silentEntity != null) {
            mc.theWorld.removeEntityFromWorld(this.silentEntity.getEntityId());
        }
        this.silentEntity = null;

        if (this.autoRefresh.get()) {
            mc.renderGlobal.loadRenderers();
        }
    }

    @EventLink
    public final Listener<EventTick> onTick = _ -> {
        if (!PlayerUtil.canUpdate()) {
            this.toggle(false);
            return;
        }

        if (this.clientEntity == null) {
            this.clientEntity = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
        }

        if (this.silentEntity == null) {
            this.silentEntity = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
            this.silentEntity.isFake = true;
            mc.theWorld.addEntityToWorld(this.silentEntity.getEntityId(), this.silentEntity);
        }

        this.clientEntity.inventory.currentItem = this.slot;

        this.silentEntity.inventory = mc.thePlayer.inventory;
        this.silentEntity.inventoryContainer = mc.thePlayer.inventoryContainer;
        this.silentEntity.setUniqueID(mc.thePlayer.getUniqueID());

        if (this.state == State.NUKER && !mc.thePlayer.capabilities.isCreativeMode) {
            this.state = State.NORMAL;
            this.slot = 0;
            Notification.send("State Reset", "Freecam nuker mode disabled due to no longer being in creative mode.", NotificationType.WARNING, 5000);
        }

    };

    @EventLink
    public final Listener<EventMovementInput> onMovementInput = event -> {

        if (this.position == null) {
            this.position = mc.thePlayer.getPositionVector();
        }

        this.prevPosition = this.position;

        this.position = this.position.add(MoveUtil.simulateSpeed(this.speed.getDouble(), mc.thePlayer.rotationYaw, event.forwards, event.strafe, false));

        if (event.jump) {
            this.position = this.position.addVector(0.0, this.speed.getDouble(), 0.0);
        }

        if (event.sneak) {
            this.position = this.position.addVector(0.0, -this.speed.getDouble(), 0.0);
        }

        event.forwards = 0.0F;
        event.strafe = 0.0F;
        event.jump = false;
        event.sneak = false;
    };

    @EventLink(EventPriorities.VERY_LOW)
    public final Listener<EventUpdate> onUpdate = _ -> {
        if (this.serverRotation == null) {
            this.serverRotation = new Rotation(mc.thePlayer);
        }
        RotationsComponent.updateServerRotations(this.serverRotation);
    };

    @EventLink
    public final Listener<EventPreRenderWorld> onPreRender = _ -> {
        if (this.clientEntity != null) {

            this.clientEntity.setPosition(this.position.xCoord, this.position.yCoord, this.position.zCoord);
            this.clientEntity.prevPosX = this.clientEntity.lastTickPosX = this.prevPosition.xCoord;
            this.clientEntity.prevPosY = this.clientEntity.lastTickPosY = this.prevPosition.yCoord;
            this.clientEntity.prevPosZ = this.clientEntity.lastTickPosZ = this.prevPosition.zCoord;

            this.clientEntity.cameraYaw = mc.thePlayer.cameraYaw;
            this.clientEntity.cameraPitch = mc.thePlayer.cameraPitch;
            this.clientEntity.prevCameraYaw = mc.thePlayer.prevCameraYaw;
            this.clientEntity.prevCameraPitch = mc.thePlayer.prevCameraPitch;

            this.clientEntity.rotationYaw = mc.thePlayer.rotationYaw;
            this.clientEntity.rotationPitch = mc.thePlayer.rotationPitch;
            this.clientEntity.prevRotationYaw = mc.thePlayer.prevRotationYaw;
            this.clientEntity.prevRotationPitch = mc.thePlayer.prevRotationPitch;

            this.clientEntity.renderYawOffset = mc.thePlayer.renderYawOffset;
            this.clientEntity.prevRenderYawOffset = mc.thePlayer.prevRenderYawOffset;

            mc.setRenderViewEntity(this.clientEntity);
        }

        if (this.silentEntity != null) {
            this.silentEntity.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, Nonsense.component(RotationsComponent.class).serverYaw, Nonsense.component(RotationsComponent.class).serverPitch);
            this.silentEntity.prevPosX = mc.thePlayer.prevPosX;
            this.silentEntity.prevPosY = mc.thePlayer.prevPosY;
            this.silentEntity.prevPosZ = mc.thePlayer.prevPosZ;
            this.silentEntity.lastTickPosX = mc.thePlayer.lastTickPosX;
            this.silentEntity.lastTickPosY = mc.thePlayer.lastTickPosY;
            this.silentEntity.lastTickPosZ = mc.thePlayer.lastTickPosZ;
            this.silentEntity.rotationYawHead = mc.thePlayer.rotationYawHead;
            this.silentEntity.swingProgress = mc.thePlayer.swingProgress;
            this.silentEntity.prevSwingProgress = mc.thePlayer.prevSwingProgress;
            this.silentEntity.setItemInUse(mc.thePlayer.getItemInUse(), mc.thePlayer.itemInUseCount);
        }

        mc.gameSettings.thirdPersonView = 0;
    };

    @EventLink
    public final Listener<EventPreRenderGui> onPreRenderGui = _ -> {
        if (this.clientEntity != null) {
            this.copyInventoryToSilentEntity(switch (this.state) {
                case NORMAL -> this.mainInventory;
                case SPEED -> this.speedInventory;
                case NUKER -> this.nukerInventory;
            });
            mc.setRenderViewEntity(this.clientEntity);
        }
    };

    @EventLink
    public final Listener<EventPreUpdateGui> onPreUpdateGui = _ -> {
        if (this.clientEntity != null) {
            mc.setRenderViewEntity(this.clientEntity);
        }
    };

    @EventLink
    public final Listener<EventPostRenderWorld> onPostRender = _ -> mc.setRenderViewEntity(mc.thePlayer);

    @EventLink
    public final Listener<EventPostRenderGui> onPostRenderGui = _ -> mc.setRenderViewEntity(mc.thePlayer);

    @EventLink
    public final Listener<EventPostUpdateGui> onPostUpdateGui = _ -> mc.setRenderViewEntity(mc.thePlayer);

    @EventLink(EventPriorities.VERY_HIGH)
    public final Listener<EventClickAction> onClickAction = event -> {
        event.left = false;
        event.right = false;
        event.blockClick = false;
        event.postRight = false;

        if (event.leftButton || event.rightButton) {
            switch (this.state) {
                case NORMAL -> {
                    switch (this.slot) {
                        case 0 -> this.state = State.SPEED;
                        case 1 -> {
                            if (this.clientEntity != null) {
                                MovingObjectPosition intercept = RotationUtil.rayCastBlocks(new Rotation(mc.thePlayer), 9999, this.clientEntity);
                                if (intercept.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                                    this.position = new Vec3(intercept.getBlockPos()).addVector(0.5, 1.0, 0.5);
                                }
                            }
                        }
                        case 7 -> {
                            if (mc.thePlayer.capabilities.isCreativeMode) {
                                this.state = State.NUKER;
                                this.slot = 6;
                            } else {
                                Notification.send("Creative Mode Required", "You must be in creative mode to use Freecam Nuker.", NotificationType.ERROR, 5000);
                            }
                        }
                        case 8 -> this.toggle(false);
                    }
                }

                case SPEED -> {
                    this.speed.set(this.slot + 1.0);
                    this.state = State.NORMAL;
                    this.slot = 0;
                }

                case NUKER -> {

                    ItemStack item = this.nukerInventory[this.slot];

                    boolean doneNonsenseAction = false;

                    if (item.hasTagCompound()) {
                        if (item.getTagCompound().hasKey("Nonsense", 1)) {
                            byte id = item.getTagCompound().getByte("Nonsense");
                            switch (id) {
                                case 0 -> { /* TODO: nuke */ }
                                case 1 -> { /* TODO: teleport */ }
                                case 2 -> {
                                    this.state = State.NORMAL; this.slot = 7;
                                }
                            }
                            doneNonsenseAction = true;
                        }
                    }

                    if (!doneNonsenseAction) {
                        // TODO: path find and send item
                    }
                }

            }
        }

    };

    @EventLink
    public final Listener<EventChangeItem> onChangeItem = event -> {
        event.cancel();

        if (event.direction > 0) {
            event.direction = 1;
        }

        if (event.direction < 0) {
            event.direction = -1;
        }

        this.changeSlot(event.direction);
    };

    @EventLink(EventPriorities.LOW)
    public final Listener<EventSelectItem> onSelectItem = event -> {

        if (event.swapped && this.canSwitchToSlot(event.slot)) {
            this.slot = event.slot;
        }

        if (event.slot != event.prevSlot) {
            event.slot = event.prevSlot;
        }
    };

    private void changeSlot(int direction) {
        do {
            this.slot -= direction;
            if (this.slot < 0) {
                this.slot = 8;
            }

            if (this.slot > 8) {
                this.slot = 0;
            }
        } while (!this.canSwitchToSlot(this.slot));
    }

    private boolean canSwitchToSlot(int slot) {
        return switch (this.state) {
            case NORMAL -> this.mainInventory[slot] != null;
            case SPEED, NUKER -> true;
        };
    }

    private void copyInventoryToSilentEntity(ItemStack[] inventory) {

        if (inventory.length != 36) {
            throw new IllegalArgumentException("Inventory copy must be 36 slots.");
        }

        if (this.silentEntity != null) {
            System.arraycopy(inventory, 0, this.clientEntity.inventory.mainInventory, 0, 36);
        }
    }

    public ItemStack getHeldItem() {
        if (this.clientEntity == null) {
            return mc.thePlayer.inventory.getClientItem();
        }
        return this.clientEntity.inventory.getCurrentItem();
    }

    public enum State {
        NORMAL,
        SPEED,
        NUKER
    }


}
