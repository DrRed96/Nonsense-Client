package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;

@ModuleInfo(name = "Item Animations",
        description = "Modifies item animations.",
        category = ModuleCategory.VISUAL)
public class ItemAnimations extends Module {

    private final GroupProperty posGroup = new GroupProperty("Position", "Modify item position.");

    private final GroupProperty usePos = new GroupProperty("Using", "Item position while using.");
    private final NumberProperty useX = new NumberProperty("X", "Using X.", 0.0, -1.0, 1.0, 0.05);
    private final NumberProperty useY = new NumberProperty("Y", "Using Y.", 0.0, -1.0, 1.0, 0.05);
    private final NumberProperty useZ = new NumberProperty("Z", "Using Z.", 0.0, -1.0, 1.0, 0.05);

    private final GroupProperty normPos = new GroupProperty("Normal", "Normal item position.");
    private final NumberProperty normX = new NumberProperty("X", "Normal X.", 0.0, -1.0, 1.0, 0.05);
    private final NumberProperty normY = new NumberProperty("Y", "Normal Y.", 0.0, -1.0, 1.0, 0.05);
    private final NumberProperty normZ = new NumberProperty("Z", "Normal Z.", 0.0, -1.0, 1.0, 0.05);

    private final BooleanProperty swordOnly = new BooleanProperty("Sword Only", "Only transform if holding a sword.", false);

    private final GroupProperty swordGroup = new GroupProperty("Sword", "Modify sword animations.");
    private final EnumProperty<BlockAnimation> blockAnimation = new EnumProperty<>("Animation", "Sword blocking animation.", BlockAnimation.DEFAULT);
    private final NumberProperty equipAnimation = new NumberProperty("Equip Animation", "Crontrols the animation played when your item is updated.", 0.5, 0.0, 1.0, 0.05);

    private final BooleanProperty oldTransform = new BooleanProperty("Old Transform", "Applies the 1.7 item transformations", false);

    public ItemAnimations() {

        this.usePos.addProperties(this.useX, this.useY, this.useZ);
        this.normPos.addProperties(this.normX, this.normY, this.normZ);

        this.posGroup.addProperties(this.usePos, this.normPos, this.swordOnly);

        this.swordGroup.addProperties(this.blockAnimation, this.equipAnimation);
        this.addProperties(this.posGroup, this.swordGroup, this.oldTransform);
    }

    public void transformFirstPersonItem(float equipProgress, float swingProgress) {

        float swingSq = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float swingSqrt = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);

        GlStateManager.translate(0.56F, -0.52F, -0.72F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(this.oldTransform.get() ? 50.0F : 45.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(swingSq * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(swingSqrt * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(swingSqrt * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    public void renderItem(ItemRenderer renderer, AbstractClientPlayer player, ItemStack itemToRender, float equipProgress, float swingProgress, float partialTicks) {

        if (player.getItemInUseCount() > 0) {

            if (!this.swordOnly.get() || itemToRender.getItemUseAction() == EnumAction.BLOCK) {
                GlStateManager.translate(this.useX.getFloat(), this.useY.getFloat(), this.useZ.getFloat());
            }

            switch (itemToRender.getItemUseAction()) {
                case NONE -> this.transformFirstPersonItem(equipProgress, 0.0F);

                case EAT, DRINK -> {
                    renderer.doEatTransformations(player, partialTicks);
                    this.transformFirstPersonItem(equipProgress, 0.0F);
                }

                case BLOCK -> {

                    final float equip = equipProgress * this.equipAnimation.getFloat();
                    final float factor1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * MathHelper.PI);
                    final float factor2 = MathHelper.sin((float) (swingProgress * swingProgress * Math.PI));

                    switch (this.blockAnimation.get()) {
                        case DEFAULT -> {
                            this.transformFirstPersonItem(equip, swingProgress);
                            renderer.doBlockTransformations();
                        }

                        case OLD -> {
                            this.transformFirstPersonItem(equip + 0.05F, swingProgress);
                            GlStateManager.translate(-0.5F, 0.5F, 0.0F);
                            GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
                            GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
                            GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
                        }

                        case EXHIBITION -> {
                            this.transformFirstPersonItem(equip, 0.0F);
                            GlStateManager.rotate(-factor1 * 20.0F, factor1 / 2.0F, 0.0F, 9.0F);
                            GlStateManager.rotate(-factor1 * 30.0F, 1.0F, factor1 / 2.0F, 0.0F);
                            renderer.doBlockTransformations();
                        }

                        case SWONG -> {
                            this.transformFirstPersonItem(equip, 0.0F);
                            GlStateManager.rotate(-factor1 * 40.0F, factor1 / 2.0F, 0.0F, 9.0F);
                            GlStateManager.rotate(-factor1 * 60.0F, 0.9F, factor1 / 2.0F, 0.0F);
                            renderer.doBlockTransformations();
                        }

                        case SWANG -> {
                            this.transformFirstPersonItem(equip, swingProgress);
                            GlStateManager.rotate(factor1 * 30.0F / 2.0F, -factor1, -0.0F, 9.0F);
                            GlStateManager.rotate(factor1 * 40.0F, 1.0F, -factor1 / 2.0F, -0.0F);
                            renderer.doBlockTransformations();
                        }

                        case SWANK -> {
                            this.transformFirstPersonItem(equip, swingProgress);
                            GlStateManager.rotate(factor1 * 30.0F, -factor1, -0.0F, 9.0F);
                            GlStateManager.rotate(factor1 * 40.0F, 1.0F, -factor1, -0.0F);
                            renderer.doBlockTransformations();
                        }

                        case TAP -> {
                            this.transformFirstPersonItem(equip, 0.0F);
                            GlStateManager.rotate(-factor2 * 40.0F / 2.0F, factor2 / 2.0F, -0.0F, 9.0F);
                            GlStateManager.rotate(-factor2 * 30.0F, 1.0F, factor2 / 2.0F, -0.0F);
                            renderer.doBlockTransformations();
                        }

                        case CHIP -> {
                            this.transformFirstPersonItem(equip, -0.2F);
                            GlStateManager.rotate(-factor2 / 19.0F, factor2 / 20.0F, -0.0F, 9.0F);
                            GlStateManager.rotate(-factor2 * 30.0F, 10.0F, factor2 / 50.0F, 0.0F);
                            renderer.doBlockTransformations();
                        }

                        case SMOOTH -> {
                            this.transformFirstPersonItem(equip, 0.0F);
                            renderer.doBlockTransformations();
                            GlStateManager.rotate(-factor1 * 140.0f, 8.0f, 0.0f, 8.0f);
                            GlStateManager.rotate(factor1 * 90.0f, 8.0f, 0.0f, 8.0f);
                        }

                        case PUNCH -> {
                            this.transformFirstPersonItem(equip, 0.0F);
                            renderer.doBlockTransformations();
                            GlStateManager.rotate(-factor1 * 30.0f, -5.0f, 0.0f, 9.0f);
                            GlStateManager.rotate(-factor1 * 10.0f, 1.0f, -0.4f, -0.5f);
                        }

                        case BUTTER -> {
                            GlStateManager.translate(0.56F, -0.52F, -0.72F);
                            GlStateManager.translate(0.0F, equip * -0.6F, 0.0F);
                            GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                            GlStateManager.rotate(factor2 * -20.0F, 0.0F, 1.0F, 0.0F);
                            GlStateManager.rotate(factor1 * -20.0F, 0.0F, 0.0F, 1.0F);
                            GlStateManager.rotate(factor1 * -20.0F, 1.0F, 0.0F, 0.0F);
                            GlStateManager.scale(0.4F, 0.4F, 0.4F);
                            renderer.doBlockTransformations();
                        }

                        case DORT -> {
                            final float dort = MathHelper.sin((float) (swingProgress * swingProgress * Math.PI - 3));

                            this.transformFirstPersonItem(equip, 1.0F);
                            GlStateManager.rotate(-factor1 * 10, 0.0f, 15.0f, 200.0f);
                            GlStateManager.rotate(-factor1 * 10f, 300.0f, factor1 / 2.0f, 1.0f);
                            renderer.doBlockTransformations();
                            GlStateManager.translate(2.4, 0.3, 0.5);
                            GlStateManager.translate(-2.10f, -0.2f, 0.1f);
                            GlStateManager.rotate(dort * 13.0f, -10.0f, -1.4f, -10.0f);
                        }

                        case DOWN -> {
                            this.transformFirstPersonItem(equip, 0.0F);
                            renderer.doBlockTransformations();
                            GlStateManager.translate(-0.05f, 0.2f, 0.2f);
                            GlStateManager.rotate(-factor1 * 70.0f / 2.0f, -8.0f, -0.0f, 9.0f);
                            GlStateManager.rotate(-factor1 * 70.0f, 1.0f, -0.4f, -0.0f);
                        }

                        case SLIDE -> {
                            this.transformFirstPersonItem(equip, 0.0F);
                            GlStateManager.rotate(factor1 * 45.0F, -factor1, 0.3F, 0.0F);
                            renderer.doBlockTransformations();
                        }

                        case SIGMA -> {
                            this.transformFirstPersonItem(equip, 0.0F);
                            GlStateManager.rotate(-factor1 * 55 / 2.0F, -8.0F, -0.0F, 9.0F);
                            GlStateManager.rotate(-factor1 * 45, 1.0F, factor1 / 2, -0.0F);
                            renderer.doBlockTransformations();
                            GlStateManager.translate(1.2, 0.3, 0.5);
                            GlStateManager.translate(-1, this.mc.thePlayer.isSneaking() ? -0.1F : -0.2F, 0.2F);
                        }

                        case LEAKED -> {
                            this.transformFirstPersonItem(equip, 0.0F);
                            GlStateManager.scale(0.8f, 0.8f, 0.8f);
                            GlStateManager.translate(0, 0.1f, 0);
                            renderer.doBlockTransformations();
                            GlStateManager.rotate(factor1 * 35.0f / 2.0f, 0.0f, 1, 1.5f);
                            GlStateManager.rotate(-factor1 * 135.0f / 4.0f, 1, 1, 0.0f);
                        }

                        case REACTOR -> {
                            this.transformFirstPersonItem(equip, 0.0F);
                            GlStateManager.rotate(-factor1 * 20.0F, factor1 / 2.0F, 0.0F, 9.0F);
                            GlStateManager.rotate(-factor1 * 30.0F, 1.0F, factor1 / 2, 0.0F);
                            GlStateManager.rotate(-factor1 * 42.0F, 1.5F, (factor1 / 1.2f), 0F);
                            renderer.doBlockTransformations();
                        }

                        default -> {
                            this.transformFirstPersonItem(equip, 0.0F);
                            renderer.doBlockTransformations();
                        }

                    }
                }

                case BOW -> {
                    this.transformFirstPersonItem(equipProgress, 0.0F);
                    renderer.doBowTransformations(partialTicks, player);
                }
            }

        } else {

            if (!this.swordOnly.get() || itemToRender.getItemUseAction() == EnumAction.BLOCK) {
                GlStateManager.translate(this.normX.getFloat(), this.normY.getFloat(), this.normZ.getFloat());
            }

            renderer.doSwingTransformations(swingProgress);
            this.transformFirstPersonItem(equipProgress, swingProgress);
        }

    }

    private enum BlockAnimation {
        DEFAULT,
        OLD,
        EXHIBITION,
        SWONG,
        SWANG,
        SWANK,
        TAP,
        CHIP,
        SMOOTH,
        PUNCH,
        BUTTER,
        DORT,
        DOWN,
        SLIDE,
        SIGMA,
        LEAKED,
        REACTOR,
        NONE
    }

}
