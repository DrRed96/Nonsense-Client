package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import optifine.Config;
import optifine.CustomItems;
import shadersmod.client.Shaders;
import shadersmod.client.ShadersRender;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.impl.visual.Capes;
import wtf.bhopper.nonsense.util.render.CapeLocation;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class LayerCape implements LayerRenderer
{
    protected static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private final RenderPlayer playerRenderer;

    public LayerCape(RenderPlayer playerRendererIn)
    {
        this.playerRenderer = playerRendererIn;
    }

    public void doRenderLayer(AbstractClientPlayer player, float partialTicks)
    {
        Capes module = Nonsense.module(Capes.class);
        CapeLocation cape = player.getLocationCape();
        if (player.hasPlayerInfo() && !player.isInvisible() && player.isWearing(EnumPlayerModelParts.CAPE) && cape != null)
        {
            try {
                this.renderCape(player, cape.cape, partialTicks, Color.WHITE, false);
                if (cape.overlay != null) {
                    GlStateManager.enableAlpha();
                    this.renderCape(player, cape.overlay, partialTicks, cape.overlayColor, true);
                    GlStateManager.disableAlpha();
                }

                if (player.isClientPlayer() && module.isToggled() && module.glint.get()) {
                    this.renderEnchanted(player, partialTicks);
                }
            } catch (Exception ignored) {}
        }
    }

    private void renderCape(AbstractClientPlayer player, ResourceLocation location, float partialTicks, Color color, boolean blend) {
        GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);

        if (blend) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        }

        this.playerRenderer.bindTexture(location);
        this.renderCape(player, partialTicks);

        if (blend) {
            GlStateManager.disableBlend();
        }
    }

    private void renderCape(AbstractClientPlayer player, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 0.125F);
        double x = player.prevChasingPosX + (player.chasingPosX - player.prevChasingPosX) * (double)partialTicks - (player.prevPosX + (player.posX - player.prevPosX) * (double)partialTicks);
        double y = player.prevChasingPosY + (player.chasingPosY - player.prevChasingPosY) * (double)partialTicks - (player.prevPosY + (player.posY - player.prevPosY) * (double)partialTicks);
        double z = player.prevChasingPosZ + (player.chasingPosZ - player.prevChasingPosZ) * (double)partialTicks - (player.prevPosZ + (player.posZ - player.prevPosZ) * (double)partialTicks);
        float yaw = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
        double d3 = MathHelper.sin(yaw * (float)Math.PI / 180.0F);
        double d4 = -MathHelper.cos(yaw * (float)Math.PI / 180.0F);
        float f1 = (float)y * 10.0F;
        f1 = MathHelper.clamp_float(f1, -6.0F, 32.0F);
        float f2 = (float)(x * d3 + z * d4) * 100.0F;
        float f3 = (float)(x * d4 - z * d3) * 100.0F;

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }

        if (f2 > 165.0F)
        {
            f2 = 165.0F;
        }

        if (f1 < -5.0F)
        {
            f1 = -5.0F;
        }

        float f4 = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * partialTicks;
        f1 = f1 + MathHelper.sin((player.prevDistanceWalkedModified + (player.distanceWalkedModified - player.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;

        if (player.isSneaking())
        {
            f1 += 25.0F;
            GlStateManager.translate(0.0F, 0.142F, -0.0178F);
        }

        GlStateManager.rotate(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        this.playerRenderer.getMainModel().renderCape(0.0625F);
        GlStateManager.popMatrix();
    }

    private void renderEnchanted(AbstractClientPlayer entitylivingbaseIn, float partialTicks)
    {
        if (!Config.isCustomItems() || CustomItems.isUseGlint())
        {
            if (!Config.isShaders() || !Shaders.isShadowPass)
            {
                float f = (float)entitylivingbaseIn.ticksExisted + partialTicks;
                this.playerRenderer.bindTexture(ENCHANTED_ITEM_GLINT_RES);

                if (Config.isShaders())
                {
                    ShadersRender.renderEnchantedGlintBegin();
                }

                GlStateManager.enableBlend();
                GlStateManager.depthFunc(GL_EQUAL);
                GlStateManager.depthMask(false);
                float f1 = 0.5F;
                GlStateManager.color(f1, f1, f1, 1.0F);

                for (int i = 0; i < 2; ++i)
                {
                    GlStateManager.disableLighting();
                    GlStateManager.blendFunc(GL_SRC_COLOR, GL_ONE);
                    float colorFactor = 0.76F;
                    GlStateManager.color(0.5F * colorFactor, 0.25F * colorFactor, 0.8F * colorFactor, 1.0F);
                    GlStateManager.matrixMode(GL_TEXTURE);
                    GlStateManager.loadIdentity();
                    float scale = 0.33333334F;
                    GlStateManager.scale(scale, scale, scale);
                    GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
                    GlStateManager.matrixMode(GL_MODELVIEW);
                    this.renderCape(entitylivingbaseIn, partialTicks);
                }

                GlStateManager.matrixMode(GL_TEXTURE);
                GlStateManager.loadIdentity();
                GlStateManager.matrixMode(GL_MODELVIEW);
                GlStateManager.enableLighting();
                GlStateManager.depthMask(true);
                GlStateManager.depthFunc(GL_LEQUAL);
                GlStateManager.disableBlend();

                if (Config.isShaders())
                {
                    ShadersRender.renderEnchantedGlintEnd();
                }
            }
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        this.doRenderLayer((AbstractClientPlayer)entitylivingbaseIn, partialTicks);
    }
}
