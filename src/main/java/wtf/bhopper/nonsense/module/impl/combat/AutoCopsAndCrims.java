package wtf.bhopper.nonsense.module.impl.combat;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.packet.EventSendPacket;
import wtf.bhopper.nonsense.event.impl.player.interact.EventPostClick;
import wtf.bhopper.nonsense.event.impl.player.EventPreMotion;
import wtf.bhopper.nonsense.event.impl.player.EventUpdate;
import wtf.bhopper.nonsense.event.impl.render.EventRenderWorld;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.player.Rotation;
import wtf.bhopper.nonsense.util.minecraft.player.RotationUtil;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.RenderUtil;

import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINES;

@ModuleInfo(name = "Auto Cops And Crims", description = "Aims for you in cops and crims", category = ModuleCategory.COMBAT)
public class AutoCopsAndCrims extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Aiming method", Mode.DYNAMIC);
    private final EnumProperty<Sorting> sorting = new EnumProperty<>("Sorting", "Target sorting method", Sorting.ANGLE);
    private final NumberProperty range = new NumberProperty("Range", "Target range", 100.0F, 20.0F, 250.0F, 10.0F, NumberProperty.FORMAT_DISTANCE);

    private EntityLivingBase target = null;
    private boolean firstShot = true;

    public AutoCopsAndCrims() {
        this.autoAddProperties();
        this.setSuffix(this.mode::getDisplayValue);
    }

    @Override
    public void onEnable() {
        this.target = null;
        this.firstShot = true;
    }

    @EventLink
    public final Listener<EventUpdate> onUpdate = _ -> {
        List<EntityLivingBase> entities = mc.theWorld.getEntities(EntityLivingBase.class, this::isValidTarget);
        this.sortTargets(entities);

        for (EntityLivingBase entity : entities) {
            if (mc.theWorld.rayTraceBlocks(mc.thePlayer.getPositionVector().addVector(0.0, mc.thePlayer.getEyeHeight(), 0.0),
                    entity.getPositionVector().addVector(0.0, entity.getEyeHeight(), 0.0),
                    false, true, false
            ) == null) {
                this.target = entity;
                break;
            }
        }

    };

    @EventLink
    public final Listener<EventPreMotion> onPre = event -> {
        if (this.target == null) {
            return;
        }

        double x = this.target.posX - 0.5 + (this.target.posX - this.target.lastTickPosX) * 5.5;
        double y = this.target.posY + this.target.getEyeHeight() - 0.35;
        double z = this.target.posZ - 0.5 + (this.target.posZ - this.target.lastTickPosZ) * 5.5;

        Rotation rotation = RotationUtil.getRotations(x, y, z);

        if (this.firstShot || this.mode.is(Mode.HEADSHOT)) {
            event.setRotations(rotation);
            return;
        }

        ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
        if (stack != null) {
            if (stack.getItem() instanceof ItemSpade) {
                rotation.pitch += 4.2F;
            } else if (stack.getItem() instanceof ItemHoe) {
                rotation.pitch += 6.5F;
            }
        }

        event.setRotations(rotation);

    };

    @EventLink
    public final Listener<EventSendPacket> onSendPacket = event -> {
        if (this.target != null) {
            if (event.packet instanceof C08PacketPlayerBlockPlacement) {
                this.firstShot = false;
            }
        }
    };

    @EventLink
    public final Listener<EventPostClick> onPostClick = event -> {
        if (event.button == EventPostClick.Button.LEFT) {
            this.firstShot = true;
        }
    };

    @EventLink
    public final Listener<EventRenderWorld> onRender = event -> {

        if (this.target == null) {
            return;
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();

        Vec3 pos = RenderUtil.renderPos(this.target, event.delta).subtract(RenderUtil.renderPos(event.delta));

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(2.0F);

        RenderUtil.glColor(ColorUtil.RED);
        renderer.begin(GL_LINES, DefaultVertexFormats.POSITION);
        renderer.pos(0.0F, mc.thePlayer.getEyeHeight(), 0.0F).endVertex();
        renderer.pos(pos.xCoord, pos.yCoord, pos.zCoord).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    };

    private boolean isValidTarget(EntityLivingBase target) {
        if (target == null) {
            return false;
        }

        return mc.thePlayer.getDistanceToEntity(target) <= this.range.getFloat();
    }

    private void sortTargets(List<EntityLivingBase> targets) {
        targets.sort(switch (this.sorting.get()) {
            case ANGLE ->
                    Comparator.comparingDouble(entity -> Math.abs(RotationUtil.getYawChange(entity.posX, entity.posZ)));
            case RANGE -> Comparator.comparingDouble(entity -> entity.getDistanceToEntity(mc.thePlayer));
            case HEALTH -> Comparator.comparingDouble(EntityLivingBase::getHealth);
        });
    }

    private enum Mode {
        DYNAMIC,
        HEADSHOT
    }

    private enum Sorting {
        ANGLE,
        RANGE,
        HEALTH,
    }

}
