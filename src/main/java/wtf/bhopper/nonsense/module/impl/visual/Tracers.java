package wtf.bhopper.nonsense.module.impl.visual;

import com.google.common.base.Predicate;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjglx.opengl.Display;
import org.lwjglx.opengl.GLSync;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventRender3D;
import wtf.bhopper.nonsense.event.impl.EventRenderGui;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.impl.combat.AntiBot;
import wtf.bhopper.nonsense.module.property.impl.*;
import wtf.bhopper.nonsense.util.misc.MathUtil;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.RenderUtil;

import javax.vecmath.Vector3d;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "Tracers", description = "Draws a line to nearby entities", category = ModuleCategory.VISUAL)
public class Tracers extends Module {

    private final GroupProperty targetsGroup = new GroupProperty("Targets", "What entities Tracers should render");
    private final BooleanProperty players = new BooleanProperty("Players", "Target Players.", true);
    private final BooleanProperty mobs = new BooleanProperty("Mobs", "Target Mobs (Zombies, Skeletons, etc.)", false);
    private final BooleanProperty animals = new BooleanProperty("Animals", "Target Animals (Pigs, Cows, etc.)", false);
    private final BooleanProperty others = new BooleanProperty("Others", "Target other entities", false);
    private final BooleanProperty invis = new BooleanProperty("Invisible", "Target invisible entities", true);

    private final EnumProperty<ColorMode> colorMode = new EnumProperty<>("Color Mode", "What color to use for the tracers.", ColorMode.DISTANCE);
    private final ColorProperty color = new ColorProperty("Color", "Color of the tracers.", ColorUtil.RED, () -> this.colorMode.is(ColorMode.STATIC));
    private final NumberProperty lineWidth = new NumberProperty("Line Width", "Width of the tracer lines", 1.5F, 0.5F, 3.0F, 0.5F);

    public Tracers() {
        this.targetsGroup.addProperties(this.players, this.mobs, this.animals, this.others, this.invis);
        this.addProperties(this.targetsGroup, this.colorMode, this.color, this.lineWidth);
    }

    @EventLink
    public final Listener<EventRender3D> onRender = event -> {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();

        Vec3 renderPos = RenderUtil.renderPos(event.delta);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(this.lineWidth.getFloat());

        for (EntityLivingBase entity : mc.theWorld.getEntities(EntityLivingBase.class, this::isValidEntity)) {
            double x = MathUtil.lerp(entity.posX, entity.lastTickPosX, event.delta) + renderPos.xCoord;
            double y = MathUtil.lerp(entity.posY, entity.lastTickPosY, event.delta) + renderPos.yCoord;
            double z = MathUtil.lerp(entity.posZ, entity.lastTickPosZ, event.delta) + renderPos.zCoord;

            RenderUtil.glColor(this.color);
            renderer.begin(GL_LINES, DefaultVertexFormats.POSITION);
            renderer.pos(0.0F, mc.thePlayer.getEyeHeight(), 0.0F).endVertex();
            renderer.pos(x, y, z).endVertex();
            tessellator.draw();

        }

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    };

    private boolean isValidEntity(EntityLivingBase entity) {

        if (entity == null || entity == mc.thePlayer) {
            return false;
        }

        if (Nonsense.module(AntiBot.class).isBot(entity)) {
            return false;
        }

        if (entity.isInvisible() && !invis.get()) {
            return false;
        }

        if (entity instanceof EntityPlayer) {
            return players.get();

        } else if (entity instanceof EntityMob) {
            return mobs.get();

        } else if (entity instanceof EntityAnimal) {
            return animals.get();
        }

        return others.get();
    }

    private enum ColorMode {
        STATIC,
        DISTANCE
    }

}