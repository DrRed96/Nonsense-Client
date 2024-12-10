package wtf.bhopper.nonsense.module.impl.combat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.*;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.annotations.Description;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.BlinkUtil;
import wtf.bhopper.nonsense.util.minecraft.PacketUtil;
import wtf.bhopper.nonsense.util.minecraft.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.RotationUtil;


@ModuleInfo(name = "Auto Block", description = "Automatically blocks your sword", category = ModuleCategory.COMBAT)
public class AutoBlock extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Autoblock method", Mode.BLOCK);

    private final GroupProperty targetsGroup = new GroupProperty("Targets", "What entities Kill Aura should target");
    private final BooleanProperty players = new BooleanProperty("Players", "Target Players.", true);
    private final BooleanProperty mobs = new BooleanProperty("Mobs", "Target Mobs (Zombies, Skeletons, etc.)", false);
    private final BooleanProperty animals = new BooleanProperty("Animals", "Target Animals (Pigs, Cows, etc.)", false);
    private final BooleanProperty others = new BooleanProperty("Others", "Target other entities", false);
    private final BooleanProperty invis = new BooleanProperty("Invisible", "Target invisible entities", true);
    private final BooleanProperty dead = new BooleanProperty("Dead", "Target dead entities", false);
    private final BooleanProperty teams = new BooleanProperty("Teams", "Prevents you from attacking teammates", true);

    private final NumberProperty range = new NumberProperty("Range", "Auto block range", 7.0, 0.0, 16.0, 0.05, NumberProperty.FORMAT_DISTANCE);
    private final BooleanProperty noSlow = new BooleanProperty("No Slow", "Applies No Slow to the sword blocking", true);
    private final BooleanProperty auraOnly = new BooleanProperty("Kill Aura Only", "Only blocks when Kill Aura is enabled", true);
    private final EnumProperty<BlockPacket> blockPacket = new EnumProperty<>("Block Packet", "When to send the block packet.", BlockPacket.PRE, () -> false);
    private final BooleanProperty blink = new BooleanProperty("Blink", "uses blink to help make auto-block full", false, () -> this.mode.is(Mode.LEGIT));

    private boolean blocking = false;
    private MovingObjectPosition mouseOver = null;

    public AutoBlock() {
        this.targetsGroup.addProperties(this.players, this.mobs, this.animals, this.others, this.invis, this.dead, this.teams);
        this.addProperties(this.mode, this.targetsGroup, this.range, this.noSlow, this.auraOnly, this.blockPacket, this.blink);
        this.setSuffix(() -> {
            if (this.mode.is(Mode.LEGIT) && this.blink.get()) {
                return "Blink";
            }

            return this.mode.getDisplayValue();
        });
    }

    @Override
    public void onEnable() {
        this.blocking = false;
        this.mouseOver = null;
    }

    @Override
    public void onDisable() {
        this.blocking = false;
        this.mouseOver = null;
    }

    @EventLink
    public final Listener<EventPreClick> onPreClick = event -> {

        if (this.canBlock() && event.button == EventPreClick.Button.RIGHT && !event.artificial) {
            event.cancel();
            return;
        }

        switch (this.mode.get()) {
            case NCP -> {
                if (event.button == EventPreClick.Button.LEFT && this.blocking && this.blockItem()) {
                    PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    this.blocking = false;
                    if (event.mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                        this.mouseOver = event.mouseOver;
                    }
                }
            }

            case PACKET -> {
                if (event.button == EventPreClick.Button.LEFT && this.blocking && this.blockItem()) {
                    if (event.mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                        this.mouseOver = event.mouseOver;
                    }
                }
            }

            case LEGIT -> {
                if (event.button == EventPreClick.Button.LEFT && this.blocking && this.blockItem()) {

                    if (this.canBlock() && this.blink.get() && !BlinkUtil.isBlinking()) {
                        BlinkUtil.enableBlink();
                    }

                    PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    event.cancel();
                    this.blocking = false;

                    if (event.mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                        this.mouseOver = event.mouseOver;
                    }
                }
            }
        }
    };

    @EventLink
    public final Listener<EventPostClick> onPostClick = event -> {

        switch (this.mode.get()) {

            case PACKET -> {
                if (event.button == EventPostClick.Button.LEFT && this.canBlock() && this.blockPacket.is(BlockPacket.PRE)) {

                    if (event.mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                        if (this.mouseOver != null) {
                            PacketUtil.send(new C02PacketUseEntity(this.mouseOver.entityHit, this.mouseOver.hitVec));
                            PacketUtil.send(new C02PacketUseEntity(this.mouseOver.entityHit, C02PacketUseEntity.Action.INTERACT));
                            this.mouseOver = null;
                        }

                        PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    }
                }
            }

            case LEGIT -> {
                if (event.button == EventPostClick.Button.LEFT) {

                    if (this.blink.get() && BlinkUtil.isBlinking() && !this.blocking) {
                        BlinkUtil.disableBlink();
                    }

                    if (this.canBlock() && event.mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && !this.blocking) {

                        if (this.mouseOver != null) {
                            PacketUtil.send(new C02PacketUseEntity(this.mouseOver.entityHit, this.mouseOver.hitVec));
                            PacketUtil.send(new C02PacketUseEntity(this.mouseOver.entityHit, C02PacketUseEntity.Action.INTERACT));
                            this.mouseOver = null;
                        }

                        PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        this.blocking = true;
                    }
                }
            }

        }
    };

    @EventLink
    public final Listener<EventUpdate> onUpdate = _ -> {

        if (this.canBlock()) {
            // Creates the client side blocking animation, will also apply slow-down
            mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 72000);
        } else {
            this.mouseOver = null;
        }

        switch (this.mode.get()) {
            case BLOCK -> {
                if (this.canBlock()) {
                    PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    this.blocking = true;
                }
            }

            case NCP -> {
                if (this.blocking && this.blockItem()) {
                    PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    this.blocking = false;
                }
            }
        }
    };

    @EventLink
    public final Listener<EventPostMotion> onPost = _ -> {
        switch (this.mode.get()) {

            case PACKET -> {
                if (this.canBlock() && this.blockPacket.is(BlockPacket.POST)) {

                    if (this.mouseOver != null) {
                        PacketUtil.send(new C02PacketUseEntity(this.mouseOver.entityHit, this.mouseOver.hitVec));
                        PacketUtil.send(new C02PacketUseEntity(this.mouseOver.entityHit, C02PacketUseEntity.Action.INTERACT));
                        this.mouseOver = null;
                    }

                    PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    this.blocking = true;
                }
            }


            case NCP -> {
                if (!this.blocking && this.canBlock()) {

                    if (this.mouseOver != null) {
                        PacketUtil.send(new C02PacketUseEntity(this.mouseOver.entityHit, this.mouseOver.hitVec));
                        PacketUtil.send(new C02PacketUseEntity(this.mouseOver.entityHit, C02PacketUseEntity.Action.INTERACT));
                        this.mouseOver = null;
                    }

                    PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    this.blocking = true;
                }
            }
        }
    };

    @EventLink
    public final Listener<EventReleaseItem> onRelease = event -> {
        if (this.canBlock()) {
            event.cancel();
        }
    };

    @EventLink
    public final Listener<EventSlowDown> onSlow = event -> {
        if (this.canBlock() && this.noSlow.get()) {
            event.cancel();
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

        if (entity == mc.thePlayer || entity == null) {
            return false;
        }

        if (Nonsense.module(AntiBot.class).isBot(entity)) {
            return false;
        }

        if (entity.isInvisible() && !invis.get()) {
            return false;
        }

        if (entity.isDead && !dead.get()) {
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

    private enum Mode {
        BLOCK,
        PACKET,
        @DisplayName("NCP") NCP,
        @Description("Will limit your APS to 10") LEGIT,
        @Description("Enable the 'No Slow' option with this") FAKE
    }

    private enum BlockPacket {
        PRE,
        POST
    }

}
