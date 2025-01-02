package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.component.impl.SilentRotationsComponent;
import wtf.bhopper.nonsense.event.EventPriorities;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.event.impl.player.movement.EventMovementInput;
import wtf.bhopper.nonsense.event.impl.player.interact.EventPreClick;
import wtf.bhopper.nonsense.event.impl.player.EventPreMotion;
import wtf.bhopper.nonsense.event.impl.render.EventPostRenderWorld;
import wtf.bhopper.nonsense.event.impl.render.EventPreRenderWorld;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.player.Rotation;

@ModuleInfo(name = "Free Camera",
        description = "Silent spectator mode",
        category = ModuleCategory.VISUAL,
        searchAlias = "Free Cam")
public class FreeCamera extends Module {

    private final NumberProperty speed = new NumberProperty("Speed", "Freecam fly speed", 4.0, 1.0, 10.0, 1.0);
    private final BooleanProperty autoRefresh = new BooleanProperty("Auto Refresh", "Automatically refresh chunks when Free Camera is disabled", false);

    private Vec3 position = null;
    private Vec3 prevPosition = null;
    private Rotation serverRotation = null;

    private EntityOtherPlayerMP clientEntity = null;
    private EntityOtherPlayerMP silentEntity = null;

    public FreeCamera() {
        this.addProperties(this.speed, this.autoRefresh);
        this.setHidden(false);
    }

    @Override
    public void onEnable() {
        this.serverRotation = null;
        this.position = null;
        this.clientEntity = null;
        this.silentEntity = null;
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
        } else {
            if (this.clientEntity == null) {
                this.clientEntity = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
            }
            if (this.silentEntity == null) {
                this.silentEntity = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
                this.silentEntity.isFake = true;
                mc.theWorld.addEntityToWorld(this.silentEntity.getEntityId(), this.silentEntity);
            }

            this.silentEntity.inventory = mc.thePlayer.inventory;
            this.silentEntity.inventoryContainer = mc.thePlayer.inventoryContainer;
            this.silentEntity.setUniqueID(mc.thePlayer.getUniqueID());
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
    public final Listener<EventPreMotion> onPre = event -> {
        if (this.serverRotation == null) {
            this.serverRotation = new Rotation(mc.thePlayer);
        }

        event.setRotations(this.serverRotation);
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
            this.silentEntity.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, Nonsense.component(SilentRotationsComponent.class).serverYaw, Nonsense.component(SilentRotationsComponent.class).serverPitch);
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
    public final Listener<EventPostRenderWorld> onPostRender = _ -> {
        mc.setRenderViewEntity(mc.thePlayer);
    };

    @EventLink
    public final Listener<EventPreClick> onPreClick = event -> {
        // Prevent the player from interacting while in Freecam
        if (!event.artificial) {
            event.cancel();
        }
    };

}
