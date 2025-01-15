package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.*;
import net.minecraft.util.*;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.render.EventRenderWorld;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.ColorProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.world.EntityUtil;
import wtf.bhopper.nonsense.util.misc.MathUtil;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.RenderUtil;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "Trajectories",
        description = "Shows where an arrow or throwable item will land.",
        category = ModuleCategory.VISUAL)
public class Trajectories extends Module {

    private static final double rad = 0.5;

    private final ColorProperty color = new ColorProperty("Color", "Trajectories color", ColorUtil.RED);
    private final NumberProperty maxSimulations = new NumberProperty("Max Simulations", "Maximum amount of simulations trajectories is allowed to make.", 100, 20, 500, 5);

    public Trajectories() {
        this.addProperties(this.color, this.maxSimulations);
    }

    @EventLink
    public final Listener<EventRenderWorld> onRenderWorld = event -> {
        EntityUtil.PathResult result = this.predictPath(event.delta);

        if (result == null) {
            return;
        }

        List<Vec3> path = result.path();
        MovingObjectPosition intercept = result.intercept();

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();

        Vec3 renderPos = RenderUtil.renderPos(event.delta);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(2.0F);
        RenderUtil.glColor(this.color);

        renderer.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        for (Vec3 point : path) {
            Vec3 renderPoint = point.subtract(renderPos);
            renderer.pos(renderPoint.xCoord, renderPoint.yCoord, renderPoint.zCoord).endVertex();
        }
        tessellator.draw();

        if (intercept != null) {

            Vec3 end = path.getLast().subtract(renderPos);

            if (intercept.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                glLineWidth(4.0F);
            }

            EnumFacing facing = intercept.sideHit;
            if (facing == null) {
                facing = mc.thePlayer.getHorizontalFacing();
            }

            EnumFacing.Axis axis = facing.getAxis();

            Vec3[] points = switch (axis) {
                case X -> new Vec3[]{
                        new Vec3(0, rad, 0),
                        new Vec3(0, 0, rad),
                        new Vec3(0, -rad, 0),
                        new Vec3(0, 0, -rad)
                };
                case Y -> new Vec3[]{
                        new Vec3(rad, 0, 0),
                        new Vec3(0, 0, rad),
                        new Vec3(-rad, 0, 0),
                        new Vec3(0, 0, -rad)
                };
                case Z -> new Vec3[]{
                        new Vec3(rad, 0, 0),
                        new Vec3(0, rad, 0),
                        new Vec3(-rad, 0, 0),
                        new Vec3(0, -rad, 0)
                };
            };

            renderer.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION);
            for (Vec3 point : points) {
                Vec3 renderPoint = point.add(end);
                renderer.pos(renderPoint.xCoord, renderPoint.yCoord, renderPoint.zCoord).endVertex();
            }
            renderer.pos(points[0].xCoord + end.xCoord, points[0].yCoord + end.yCoord, points[0].zCoord + end.zCoord).endVertex();
            tessellator.draw();


        }

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

    };

    private EntityUtil.PathResult predictPath(float delta) {
        ItemStack stack = mc.thePlayer.getHeldItem();
        if (stack == null) {
            return null;
        }

        Item item = stack.getItem();

        if (!this.validItem(item)) {
            return null;
        }

        double posX = MathUtil.lerp(mc.thePlayer.lastTickPosX, mc.thePlayer.posX, delta);
        double posY = MathUtil.lerp(mc.thePlayer.lastTickPosY, mc.thePlayer.posY, delta) + mc.thePlayer.getEyeHeight();
        double posZ = MathUtil.lerp(mc.thePlayer.lastTickPosZ, mc.thePlayer.posZ, delta);

        float rotationYaw = MathUtil.lerp(mc.thePlayer.prevRotationYaw, mc.thePlayer.rotationYaw, delta);
        float rotationPitch = MathUtil.lerp(mc.thePlayer.prevRotationPitch, mc.thePlayer.rotationPitch, delta);

        if (item instanceof ItemBow) {
            float velocity = this.getArrowVelocity(stack);
            if (velocity < 0.3F) {
                return null;
            }
            return EntityUtil.predictProjectilePath(posX, posY, posZ, rotationYaw, rotationPitch, 0.5F, velocity, 0.05F, mc.thePlayer, this.maxSimulations.getInt());
        }

        return EntityUtil.predictThrowablePath(posX, posY, posZ, rotationYaw, rotationPitch, mc.thePlayer, this.maxSimulations.getInt());
    }

    private boolean validItem(Item item) {
        return item instanceof ItemEnderPearl || item instanceof ItemEgg || item instanceof ItemSnowball || item instanceof ItemBow;
    }

    private float getArrowVelocity(ItemStack stack) {
        int bowUseDuration = stack.getMaxItemUseDuration() - mc.thePlayer.getItemInUseCount();
        float arrayVelocity = (float) bowUseDuration / 20.0F;
        arrayVelocity = (arrayVelocity * arrayVelocity + arrayVelocity * 2.0F);

        if (arrayVelocity > 3.0F) {
            arrayVelocity = 3.0F;
        }

        return arrayVelocity;
    }


}
