package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.EventPriorities;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventRenderGui;
import wtf.bhopper.nonsense.event.impl.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.impl.combat.AntiBot;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.ColorProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.util.minecraft.PlayerUtil;
import wtf.bhopper.nonsense.util.misc.MathUtil;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.NVGHelper;
import wtf.bhopper.nonsense.util.render.RenderUtil;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.util.*;
import java.util.function.BiConsumer;

@ModuleInfo(name = "ESP", description = "Gives you extrasensory perception", category = ModuleCategory.VISUAL)
public class Esp extends Module {

    private final GroupProperty targetsGroup = new GroupProperty("Targets", "What entities Kill Aura should target");
    private final BooleanProperty players = new BooleanProperty("Players", "Target Players.", true);
    private final BooleanProperty mobs = new BooleanProperty("Mobs", "Target Mobs (Zombies, Skeletons, etc.)", false);
    private final BooleanProperty animals = new BooleanProperty("Animals", "Target Animals (Pigs, Cows, etc.)", false);
    private final BooleanProperty others = new BooleanProperty("Others", "Target other entities", false);
    private final BooleanProperty invis = new BooleanProperty("Invisible", "Target invisible entities", true);

    private final GroupProperty boxGroup = new GroupProperty("Box", "Boxes");
    private final BooleanProperty boxEnable = new BooleanProperty("Enable", "Enable boxes", true);
    private final BooleanProperty boxCorners = new BooleanProperty("Corners", "Only render corners", false);
    private final BooleanProperty boxOutline = new BooleanProperty("Outline", "Box outline", true);
    private final ColorProperty boxColor = new ColorProperty("Color", "color", 0xFFFFFFFF);

    private final GroupProperty nameGroup = new GroupProperty("Names", "Names");
    private final BooleanProperty nameEnable = new BooleanProperty("Enable", "Enable names", true);
    private final BooleanProperty displayNames = new BooleanProperty("Display Names", "Render display names", true);
    private final BooleanProperty nameHealth = new BooleanProperty("Health", "Display health in names", true);
    private final BooleanProperty nameBackground = new BooleanProperty("Background", "Display background", true);

    private final GroupProperty barGroup = new GroupProperty("Health Bar", "Health bars");
    private final BooleanProperty barEnable = new BooleanProperty("Enable", "Enable health bars", true);
    private final EnumProperty<HealthColor> barColorMode = new EnumProperty<>("Color Mode", "Color mode of the health bars.", HealthColor.HEALTH);
    private final ColorProperty barColor = new ColorProperty("Color", "Health bars color", 0xFF00FF00, () -> this.barColorMode.is(HealthColor.CUSTOM));

    private final List<RenderEntity> renderEntities = new ArrayList<>();

    public Esp() {
        this.targetsGroup.addProperties(this.players, this.mobs, this.animals, this.others, this.invis);
        this.boxGroup.addProperties(this.boxEnable, this.boxCorners, this.boxOutline, this.boxColor);
        this.nameGroup.addProperties(this.nameEnable, this.displayNames, this.nameHealth,this.nameBackground);
        this.barGroup.addProperties(this.barEnable, this.barColorMode, this.barColor);
        this.addProperties(this.targetsGroup, this.boxGroup, this.nameGroup, this.barGroup);
    }

    @EventLink
    public final Listener<EventTick> onTick = event -> {
        this.renderEntities.clear();

        if (PlayerUtil.canUpdate()) {
            this.renderEntities.addAll(mc.theWorld.getEntities(EntityLivingBase.class, this::isValidEntity)
                    .stream()
                    .collect(
                            ArrayList::new,
                            (renderEntities, entityLivingBase) -> renderEntities.add(new RenderEntity(entityLivingBase)),
                            Collection::addAll
                    )
            );
            this.renderEntities.sort(Comparator.<RenderEntity>comparingDouble(value -> mc.thePlayer.getDistanceToEntity(value.entity)).reversed());
        }
    };

    @EventLink(EventPriorities.VERY_LOW)
    public final Listener<EventRenderGui> onRenderGui = event -> {
        RenderManager renderManager = mc.getRenderManager();
        EntityRenderer entityRenderer = mc.entityRenderer;

        GlStateManager.pushMatrix();
        event.scale.scaleToFactor(1.0F);

        for (RenderEntity render : this.renderEntities) {
            render.draw(event, renderManager, entityRenderer);
        }

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

    private class RenderEntity {
        private final EntityLivingBase entity;

        private final String name;
        private final float health;
        private final float healthFactor;
        private final float absorbFactor;
        private final List<ItemStack> items = new ArrayList<>();

        private int startX;
        private int startY;
        private int endX;
        private int endY;
        private int width;
        private int height;

        public RenderEntity(EntityLivingBase entity) {
            this.entity = entity;

            this.name = displayNames.get() ? entity.getDisplayName().getFormattedText() : entity.getName();
            this.health = entity.getHealth();
            this.healthFactor = this.health / entity.getMaxHealth();
            this.absorbFactor = Math.min(entity.getAbsorptionAmount() / entity.getMaxHealth(), 1.0F);

            this.items.clear();
            this.items.add(entity.getHeldItem());
            for (int i = 0; i < 4; i++) {
                this.items.add(entity.getCurrentArmor(i));
            }
        }

        public void draw(EventRenderGui event, RenderManager render, EntityRenderer entityRenderer) {
            if (!RenderUtil.isInViewFrustum(this.entity)) {
                return;
            }

            double x = MathUtil.lerp(entity.lastTickPosX, entity.posX, event.delta);
            double y = MathUtil.lerp(entity.lastTickPosY, entity.posY, event.delta);
            double z = MathUtil.lerp(entity.lastTickPosZ, entity.posZ, event.delta);

            double width = entity.width / 1.5;
            double height = entity.height + (entity.isSneaking() ? -0.3 : 0.2);
            AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);

            List<Vector3d> vectors = Arrays.asList(
                    new Vector3d(aabb.minX, aabb.minY, aabb.minZ),
                    new Vector3d(aabb.minX, aabb.maxY, aabb.minZ),
                    new Vector3d(aabb.maxX, aabb.minY, aabb.minZ),
                    new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ),
                    new Vector3d(aabb.minX, aabb.minY, aabb.maxZ),
                    new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ),
                    new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ),
                    new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ)
            );

            entityRenderer.setupCameraTransform(event.delta, 0);
            Vector4d position = null;

            for (Vector3d vector : vectors) {
                Vector3d projected = RenderUtil.project2D(1,
                        vector.x - render.viewerPosX,
                        vector.y - render.viewerPosY,
                        vector.z - render.viewerPosZ);

                if (projected != null && projected.z >= 0.0 && projected.z < 1.0) {
                    if (position == null) {
                        position = new Vector4d(projected.x, projected.y, projected.z, 0.0);
                    }

                    position.x = Math.min(projected.x, position.x);
                    position.y = Math.min(projected.y, position.y);
                    position.z = Math.max(projected.x, position.z);
                    position.w = Math.max(projected.y, position.w);
                }
            }

            if (position == null) {
                return;
            }

            entityRenderer.setupOverlayRendering();
            this.startX = (int)position.x;
            this.startY = (int)position.y;
            this.endX = (int)position.z;
            this.endY = (int)position.w;
            this.width = this.endX - this.startX;
            this.height = this.endY - this.startY;

            this.drawBox();
        }

        public void drawBox() {
            if (!boxEnable.get()) {
                return;
            }

            int color = boxColor.getRGB();

            NVGHelper.begin();
            NVGHelper.translate(this.startX, this.startY);

            if (boxCorners.get()) {

            } else {
                if (boxOutline.get()) {
                    NVGHelper.drawRectOutline(-1.0F, -1.0F, this.width + 2.0F, this.height + 2.0F, ColorUtil.BLACK);
                    NVGHelper.drawRectOutline(1.0F, 1.0F, this.width - 2.0F, this.height - 2.0F, ColorUtil.BLACK);
                }

                NVGHelper.drawRectOutline(0.0F, 0.0F, this.width, this.height, color);
            }

            NVGHelper.end();

        }

    }

    private enum HealthColor {
        HEALTH,
        CUSTOM
    }

}
