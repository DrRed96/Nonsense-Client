package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventRender3D;
import wtf.bhopper.nonsense.event.impl.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.ColorProperty;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.misc.Clock;
import wtf.bhopper.nonsense.util.render.RenderUtil;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "Breadcrumbs", description = "Renders a line that displays where you have been", category = ModuleCategory.VISUAL)
public class Breadcrumbs extends Module {

    private final ColorProperty color = new ColorProperty("Color", "Color of the line", 0xFFFF0000);

    private final Clock timer = new Clock();
    private final List<Vec3> path = new ArrayList<>();

    public Breadcrumbs() {
        this.addProperties(this.color);
    }

    @Override
    public void onEnable() {
        this.path.clear();
        this.path.add(mc.thePlayer.getPositionVector().addVector(0.0, 0.3, 0.0));
    }

    @Override
    public void onDisable() {
        this.path.clear();
    }

    @EventLink
    public final Listener<EventTick> onTick = _ -> {
        if (!PlayerUtil.canUpdate()) {
            return;
        }

        if (this.timer.hasReached(100)) {
            if (MoveUtil.isMoving()) {
                this.path.add(mc.thePlayer.getPositionVector().addVector(0.0, 0.3, 0.0));
                this.timer.reset();
            }
        }
    };

    @EventLink
    public final Listener<EventRender3D> onRender = event -> {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();

        Vec3 renderPos = RenderUtil.renderPos(event.delta);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        glEnable(GL_LINE_SMOOTH);
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glLineWidth(2.0F);
        RenderUtil.glColor(this.color);

        renderer.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        for (Vec3 pos : this.path) {
            Vec3 render = pos.subtract(renderPos);
            renderer.pos(render.xCoord, render.yCoord, render.zCoord).endVertex();
        }
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.popMatrix();
    };

}
