package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.EventPriorities;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.render.EventRenderGui;
import wtf.bhopper.nonsense.event.impl.render.EventRenderNameTag;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.impl.combat.AntiBot;
import wtf.bhopper.nonsense.module.property.impl.*;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.misc.MathUtil;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.Fonts;
import wtf.bhopper.nonsense.util.render.NVGHelper;
import wtf.bhopper.nonsense.util.render.RenderUtil;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

@ModuleInfo(name = "ESP", description = "Gives you extrasensory perception", category = ModuleCategory.VISUAL)
public class Esp extends Module {

    private static final NumberFormat HEALTH_FORMAT = new DecimalFormat("#0.#");

    private final GroupProperty targetsGroup = new GroupProperty("Targets", "What entities ESP should render", this);
    private final BooleanProperty players = new BooleanProperty("Players", "Target Players.", true);
    private final BooleanProperty mobs = new BooleanProperty("Mobs", "Target Mobs (Zombies, Skeletons, etc.)", false);
    private final BooleanProperty animals = new BooleanProperty("Animals", "Target Animals (Pigs, Cows, etc.)", false);
    private final BooleanProperty others = new BooleanProperty("Others", "Target other entities", false);
    private final BooleanProperty invis = new BooleanProperty("Invisible", "Target invisible entities", true);
    private final BooleanProperty self = new BooleanProperty("Self", "Targets yourself while in third person", false);

    private final GroupProperty boxGroup = new GroupProperty("Box", "Boxes", this);
    private final BooleanProperty boxEnable = new BooleanProperty("Enable", "Enable boxes", true);
    private final BooleanProperty boxCorners = new BooleanProperty("Corners", "Only render corners", false);
    private final NumberProperty boxCornerFactor = new NumberProperty("Corner Factor", "Corner factor.", this.boxCorners::get, 50.0, 1.0, 100.0, 1.0, NumberProperty.FORMAT_PERCENT);
    private final BooleanProperty boxOutline = new BooleanProperty("Outline", "Box outline", true);
    private final ColorProperty boxColor = new ColorProperty("Color", "color", 0xFFFFFFFF);

    private final GroupProperty nameGroup = new GroupProperty("Names", "Names", this);
    private final BooleanProperty nameEnable = new BooleanProperty("Enable", "Enable name tags", true);
    private final ColorProperty nameColor = new ColorProperty("Color", "Color of the name tags", 0xFFFFFFFF);
    private final BooleanProperty displayNames = new BooleanProperty("Display Names", "Render display names", true);
    private final BooleanProperty nameHealth = new BooleanProperty("Health", "Display health in name tags", true);
    private final BooleanProperty nameBackground = new BooleanProperty("Background", "Display background", true);
    private final BooleanProperty nameOutline = new BooleanProperty("Outline", "Draws outlined strings", false);
    private final BooleanProperty heldItem = new BooleanProperty("Held Item", "Displays the entities held item", true);

    private final GroupProperty barGroup = new GroupProperty("Health Bar", "Health bars", this);
    private final BooleanProperty barEnable = new BooleanProperty("Enable", "Enable health bars", true);
    private final EnumProperty<HealthColor> barColorMode = new EnumProperty<>("Color Mode", "Color mode of the health bars.", HealthColor.HEALTH);
    private final ColorProperty barColor = new ColorProperty("Color", "Health bars color", 0xFF00FF00, () -> this.barColorMode.is(HealthColor.CUSTOM));

    private final List<RenderEntity> renderEntities = new ArrayList<>();

    public Esp() {
        this.targetsGroup.addProperties(this.players, this.mobs, this.animals, this.others, this.invis, this.self);
        this.boxGroup.addProperties(this.boxEnable, this.boxCorners, this.boxOutline, this.boxCornerFactor, this.boxColor);
        this.nameGroup.addProperties(this.nameEnable, this.nameColor, this.displayNames, this.nameHealth, this.nameBackground, this.nameOutline, this.heldItem);
        this.barGroup.addProperties(this.barEnable, this.barColorMode, this.barColor);
        this.addProperties(this.targetsGroup, this.boxGroup, this.nameGroup, this.barGroup);
    }

    @EventLink
    public final Listener<EventTick> onTick = _ -> {
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

    @EventLink
    public final Listener<EventRenderNameTag> onRenderNameTag = event -> {
        for (RenderEntity renderEntity : this.renderEntities) {
            if (renderEntity.entity == event.entity) {
                event.cancel();
                break;
            }
        }
    };

    private boolean isValidEntity(EntityLivingBase entity) {

        if (entity == null) {
            return false;
        }

        if (Nonsense.module(AntiBot.class).isBot(entity)) {
            return false;
        }

        if (entity.isInvisible() && !invis.get()) {
            return false;
        }

        return switch (entity) {
            case EntityPlayer _ -> {

                if (!players.get()) {
                    yield false;
                }

                if (entity.isClientPlayer()) {
                    yield self.get() && mc.gameSettings.thirdPersonView != 0;
                }

                yield true;
            }
            case EntityMob _ -> mobs.get();
            case EntityAnimal _ -> animals.get();
            default -> others.get();
        };
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
            this.drawHealthBar();
            this.drawName(event.scale);
        }

        public void drawBox() {
            if (!boxEnable.get()) {
                return;
            }

            int color = boxColor.getRGB();

            NVGHelper.begin();
            NVGHelper.translate(this.startX, this.startY);

            if (boxCorners.get()) {

                float w = this.width * boxCornerFactor.getFloat() / 200.0F;
                float h = this.height * boxCornerFactor.getFloat() / 200.0F;
                float iw = this.width - w + 1.0F;
                float ih = this.height - h + 1.0F;

                if (boxOutline.get()) {
                    // Top Left
                    NVGHelper.drawRect(-1.0F, -1.0F, w + 2.0F, 3.0F, ColorUtil.BLACK);
                    NVGHelper.drawRect(-1.0F, -1.0F, 3.0F, h + 2.0F, ColorUtil.BLACK);

                    // Top Right
                    NVGHelper.drawRect(iw - 1.0F, -1.0F, w + 2.0F, 3.0F, ColorUtil.BLACK);
                    NVGHelper.drawRect(this.width - 1.0F, -1.0F, 3.0F, h + 2.0F, ColorUtil.BLACK);

                    // Bottom Left
                    NVGHelper.drawRect(-1.0F, this.height - 1.0F, w + 2.0F, 3.0F, ColorUtil.BLACK);
                    NVGHelper.drawRect(-1.0F, ih - 1.0F, 3.0F, h + 2.0F, ColorUtil.BLACK);

                    // Bottom Right
                    NVGHelper.drawRect(iw - 1.0F, this.height  - 1.0F, w + 2.0F, 3.0F, ColorUtil.BLACK);
                    NVGHelper.drawRect(this.width - 1.0F, ih - 1.0F, 3.0F, h + 2.0F, ColorUtil.BLACK);
                }

                // Top Left
                NVGHelper.drawRect(0.0F, 0.0F, w, 1.0F, color);
                NVGHelper.drawRect(0.0F, 0.0F, 1.0F, h, color);

                // Top Right
                NVGHelper.drawRect(iw, 0.0F, w, 1.0F, color);
                NVGHelper.drawRect(this.width, 0.0F, 1.0F, h, color);

                // Bottom Left
                NVGHelper.drawRect(0.0F, this.height, w, 1.0F, color);
                NVGHelper.drawRect(0.0F, ih, 1.0F, h, color);

                // Bottom Right
                NVGHelper.drawRect(iw, this.height, w, 1.0F, color);
                NVGHelper.drawRect(this.width, ih, 1.0F, h, color);

            } else {
                if (boxOutline.get()) {
                    NVGHelper.drawRectOutline(-1.0F, -1.0F, this.width + 2.0F, this.height + 2.0F, ColorUtil.BLACK);
                    NVGHelper.drawRectOutline(1.0F, 1.0F, this.width - 2.0F, this.height - 2.0F, ColorUtil.BLACK);
                }

                NVGHelper.drawRectOutline(0.0F, 0.0F, this.width, this.height, color);
            }

            NVGHelper.end();

        }

        public void drawHealthBar() {
            if (!barEnable.get()) {
                return;
            }

            int color = switch (barColorMode.get()) {
                case HEALTH -> ColorUtil.health(this.healthFactor);
                case CUSTOM -> barColor.getRGB();
            };

            NVGHelper.begin();
            NVGHelper.translate(this.startX, this.startY);

            NVGHelper.drawRect(-5.0F, 0.0F, 3.0F, this.height + 1.0F, 0x80000000);
            if (this.healthFactor > 0.0F) {
                NVGHelper.drawRect(-5.0F, this.height - (this.height * this.healthFactor), 3.0F, this.height * this.healthFactor + 1.0F, color);
                if (this.absorbFactor > 0.0F) {
                    NVGHelper.drawRect(-5.0F, this.height - (this.height * this.absorbFactor), 3.0F, this.height * this.absorbFactor + 1.0F, Potion.absorption.getLiquidColor() | 0xFF00000);
                }
            }

            NVGHelper.drawRectOutline(-6.0F, -1.0F, 3.0F, this.height + 2.0F, 0xFF000000);

            NVGHelper.end();
        }

        public void drawName(ScaledResolution scale) {
            if (!nameEnable.get()) {
                return;
            }

            FontRenderer font = Fonts.bit();

            String display = this.name;

            if (nameHealth.get()) {
                display += " \2477[\247f" + HEALTH_FORMAT.format(this.health) + "\247c\u2764\2477]";
            }

            float w = this.width / 2.0F;
            float textWidth = font.getStringWidthF(display);
            float tagX = this.startX + w - textWidth / 2.0F;
            float tagY = startY - 10.0F;

            if (nameBackground.get()) {
                NVGHelper.begin();
                NVGHelper.drawRect(tagX - 2.0F, tagY - 3.0F, textWidth + 2.0F, 11.0F, 0x80000000);
                NVGHelper.end();
            }

            GlStateManager.pushMatrix();
            scale.scaleToOne();
            if (nameOutline.get()) {
                RenderUtil.drawOutlineString(font, display, tagX, tagY - 1.0F, nameColor.getRGB(), ColorUtil.BLACK);
            } else {
                font.drawStringWithShadow(display, tagX, tagY - 1.0F, nameColor.getRGB());
            }
            GlStateManager.popMatrix();

            if (heldItem.get() && entity.getHeldItem() != null) {
                String itemDisplay = entity.getHeldItem().getDisplayName();
                float itemWidth = font.getStringWidthF(itemDisplay);
                float itemX = this.startX + w - itemWidth / 2.0F;
                float itemY = this.endY + 6.0F;

                if (nameBackground.get()) {
                    NVGHelper.begin();
                    NVGHelper.drawRect(itemX - 2.0F, itemY - 3.0F, itemWidth + 2.0F, 11.0F, 0x80000000);
                    NVGHelper.end();
                }

                GlStateManager.pushMatrix();
                scale.scaleToOne();
                if (nameOutline.get()) {
                    RenderUtil.drawOutlineString(font, itemDisplay, itemX, itemY - 1.0F, ColorUtil.WHITE, ColorUtil.BLACK);
                } else {
                    font.drawStringWithShadow(itemDisplay, itemX, itemY - 1.0F, ColorUtil.WHITE);
                }
                GlStateManager.popMatrix();
            }

        }

    }

    private enum HealthColor {
        HEALTH,
        CUSTOM
    }

}
