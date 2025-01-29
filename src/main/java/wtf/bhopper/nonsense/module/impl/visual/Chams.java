package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.render.EventPostRenderEntity;
import wtf.bhopper.nonsense.event.impl.render.EventPreRenderEntity;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.impl.combat.AntiBot;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.ColorProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.RenderUtil;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "Chams",
        description = "Allows you to see entities through walls",
        category = ModuleCategory.VISUAL)
public class Chams extends AbstractModule {

    private final GroupProperty targetsGroup = new GroupProperty("Targets", "What entities Chams should render", this);
    private final BooleanProperty players = new BooleanProperty("Players", "Target Players.", true);
    private final BooleanProperty mobs = new BooleanProperty("Mobs", "Target Mobs (Zombies, Skeletons, etc.)", false);
    private final BooleanProperty animals = new BooleanProperty("Animals", "Target Animals (Pigs, Cows, etc.)", false);
    private final BooleanProperty others = new BooleanProperty("Others", "Target other entities", false);
    private final BooleanProperty invis = new BooleanProperty("Invisible", "Target invisible entities", true);

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Chams mode", Mode.NORMAL);
    private final ColorProperty color = new ColorProperty("Visible Color", "Color for visible entities", 0xFFFF0000, () -> this.mode.is(Mode.COLORED));
    private final ColorProperty colorInvis = new ColorProperty("Invisible Color", "Color for invisible entities", 0xFFFFFF00, () -> this.mode.is(Mode.COLORED));
    private final BooleanProperty lighting = new BooleanProperty("Lighting", "Displays lighting on the entities", false, () -> this.mode.is(Mode.COLORED));

    public Chams() {
        super();
        this.targetsGroup.addProperties(this.players, this.mobs, this.animals, this.others, this.invis);
        this.addProperties(this.targetsGroup, this.mode, this.color, this.colorInvis, this.lighting);
    }

    @EventLink
    public final Listener<EventPreRenderEntity> onPre = event -> {
        if (!this.isValidEntity(event.entity)) {
            return;
        }

        switch (this.mode.get()) {
            case NORMAL -> {
                glEnable(GL_POLYGON_OFFSET_FILL);
                glPolygonOffset(1.0F, -1100000.0F);
            }

            case COLORED -> {
                event.cancel();
                try {
                    Render render = mc.getRenderManager().getEntityRenderObject(event.entity);
                    if (render instanceof RendererLivingEntity renderLiving) {
                        glPushMatrix();
                        glDisable(GL_DEPTH_TEST);
                        glDisable(GL_TEXTURE_2D);
                        glEnable(GL_BLEND);

                        if (!this.lighting.get()) {
                            GlStateManager.disableLighting();
                        }

                        RenderUtil.glColor(this.colorInvis);
                        renderLiving.renderModel(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.rotationYawHead, event.rotationPitch, event.offset);

                        glEnable(GL_DEPTH_TEST);
                        RenderUtil.glColor(this.color);
                        renderLiving.renderModel(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.rotationYawHead, event.rotationPitch, event.offset);

                        glEnable(GL_TEXTURE_2D);
                        glDisable(GL_BLEND);
                        RenderUtil.glColor(ColorUtil.WHITE);
                        if (!this.lighting.get()) {
                            GlStateManager.enableLighting();
                        }

                        glPopMatrix();

                        renderLiving.renderLayers(event.entity, event.limbSwing, event.limbSwingAmount, mc.timer.renderPartialTicks, event.ageInTicks, event.rotationYawHead, event.rotationPitch, event.offset);
                        glPopMatrix();

                    }
                } catch (Exception ignored) {}
            }

        }
    };

    @EventLink
    public final Listener<EventPostRenderEntity> onPost = event -> {
        if (!this.isValidEntity(event.entity)) {
            return;
        }

        switch (this.mode.get()) {
            case NORMAL -> {
                glDisable(GL_POLYGON_OFFSET_FILL);
                glPolygonOffset(1.0F, 1100000.0F);
            }
        }
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

        return switch (entity) {
            case EntityPlayer ignored -> players.get();
            case EntityMob ignored -> mobs.get();
            case EntityAnimal ignored -> animals.get();
            default -> others.get();
        };

    }

    private enum Mode {
        NORMAL,
        COLORED
    }

}
