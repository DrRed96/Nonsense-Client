package wtf.bhopper.nonsense.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.glu.Project;
import wtf.bhopper.nonsense.gui.screens.altmanager.GuiAltManager;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;
import wtf.bhopper.nonsense.util.misc.InputUtil;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.Fonts;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;
import static org.lwjgl.opengl.GL11.*;

public class NonsenseMainMenu extends GuiScreen {

    private static final ResourceLocation[] PANORAMA = new ResourceLocation[]{
            new ResourceLocation("textures/gui/title/background/panorama_0.png"),
            new ResourceLocation("textures/gui/title/background/panorama_1.png"),
            new ResourceLocation("textures/gui/title/background/panorama_2.png"),
            new ResourceLocation("textures/gui/title/background/panorama_3.png"),
            new ResourceLocation("textures/gui/title/background/panorama_4.png"),
            new ResourceLocation("textures/gui/title/background/panorama_5.png")
    };

    private static final ResourceLocation LOGO = new ResourceLocation("nonsense/logo.png");

    private static List<String> splashes = null;

    private ResourceLocation backgroundTexture;
    private String splashText;
    private int panoramaTimer;

    private ScaledResolution scaledRes;

    private final Button[] buttons = new Button[]{
            new Button("Singleplayer", () -> this.mc.displayGuiScreen(new GuiSelectWorld(this))),
            new Button("Multiplayer", () -> this.mc.displayGuiScreen(new GuiMultiplayer(this))),
            new Button("Alt Manager", () -> this.mc.displayGuiScreen(new GuiAltManager(this)))
    };

    private final IconButton[] iconButtons = new IconButton[]{
            new IconButton("nonsense/icon/close.png", () -> mc.shutdown()),
            new IconButton("nonsense/icon/options.png", () -> mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings))),
            new IconButton("nonsense/icon/language.png", () -> mc.displayGuiScreen(new GuiLanguage(this, mc.gameSettings, mc.getLanguageManager())))
    };

    public NonsenseMainMenu() {
        if (splashes == null) {
            loadSplashes();
        }
        this.selectNewSplashText();
    }

    @Override
    public void initGui() {
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", new DynamicTexture(256, 256));
        this.scaledRes = new ScaledResolution(mc);

        for (int i = 0; i < this.buttons.length; i++) {
            this.buttons[i].x = (scaledRes.getScaledWidth() * scaledRes.getScaleFactor()) / 2;
            this.buttons[i].y = (scaledRes.getScaledHeight() * scaledRes.getScaleFactor()) / 2 + 60 + i * 50;
        }

        for (int i = 0; i < this.iconButtons.length; i++) {
            this.iconButtons[i].x = (scaledRes.getScaledWidth() * scaledRes.getScaleFactor()) - (i + 1) * 72;
            this.iconButtons[i].y = 8;
        }
    }

    @Override
    public void updateScreen() {
        this.panoramaTimer++;
    }

    @Override
    public void drawScreen(int ignoredX, int ignoredY, float partialTicks) {
        int[] mousePos = InputUtil.getUnscaledMousePositions();
        int width = this.scaledRes.getScaledWidth() * this.scaledRes.getScaleFactor();
        int height = this.scaledRes.getScaledHeight() * this.scaledRes.getScaleFactor();

        GlStateManager.disableAlpha();
        this.renderSkybox(partialTicks);
        GlStateManager.enableAlpha();

        GlStateManager.pushMatrix();
        this.scaledRes.scaleToFactor(1.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(width / 2.0F, height / 2.0F, 0.0F);
        GlStateManager.scale(0.5F, 0.5F, 1.0F);
        mc.getTextureManager().bindTexture(LOGO);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        drawModalRectWithCustomSizedTexture(-960, -810, 0, 0, 1920, 1080, 1920, 1080);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        NVGHelper.begin();

        NVGHelper.translate(width / 2.0F, height / 2.0F);
        NVGHelper.fontFace(Fonts.ARIAL);
        NVGHelper.fontSize(20.0F);
        NVGHelper.textAlign(NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        NVGHelper.drawText(this.splashText, 0.0F, 0.0F, ColorUtil.rainbow(1.0F, 1.0F), true);
        NVGHelper.resetTransform();

        for (Button button : this.buttons) {
            button.draw(mousePos[0], mousePos[1]);
        }
        NVGHelper.end();

        for (IconButton iconButton : this.iconButtons) {
            iconButton.draw(mousePos[0], mousePos[1]);
        }

        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int[] mousePos = InputUtil.getUnscaledMousePositions();
        if (mouseButton == 0) {
            for (Button button : this.buttons) {
                if (button.mouseIntersecting(mousePos[0], mousePos[1])) {
                    this.playPressSound(mc.getSoundHandler());
                    button.action.run();
                }
            }

            for (IconButton button : this.iconButtons) {
                if (button.mouseIntersecting(mousePos[0], mousePos[1])) {
                    this.playPressSound(mc.getSoundHandler());
                    button.action.run();
                }
            }

        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

    }

    private void drawPanorama(float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.matrixMode(GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GlStateManager.matrixMode(GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        for (int i = 0; i < 64; ++i) {
            GlStateManager.pushMatrix();
            float f = ((float) (i % 8) / 8.0F - 0.5F) / 64.0F;
            float f1 = ((float) (i / 8) / 8.0F - 0.5F) / 64.0F;
            float f2 = 0.0F;
            GlStateManager.translate(f, f1, f2);
            GlStateManager.rotate(MathHelper.sin(((float) this.panoramaTimer + partialTicks) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-((float) this.panoramaTimer + partialTicks) * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int k = 0; k < 6; ++k) {
                GlStateManager.pushMatrix();

                switch (k) {
                    case 1 -> GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                    case 2 -> GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                    case 3 -> GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                    case 4 -> GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                    case 5 -> GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                this.mc.getTextureManager().bindTexture(PANORAMA[k]);
                worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                int alpha = 255 / (i + 1);
                worldRenderer.pos(-1.0, -1.0, 1.0).tex(0.0, 0.0).color(255, 255, 255, alpha).endVertex();
                worldRenderer.pos(1.0, -1.0, 1.0).tex(1.0, 0.0).color(255, 255, 255, alpha).endVertex();
                worldRenderer.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).color(255, 255, 255, alpha).endVertex();
                worldRenderer.pos(-1.0, 1.0, 1.0).tex(0.0, 1.0).color(255, 255, 255, alpha).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }

        worldRenderer.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(GL_PROJECTION);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL_MODELVIEW);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }

    private void rotateAndBlurSkybox() {
        this.mc.getTextureManager().bindTexture(this.backgroundTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        GlStateManager.colorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        GlStateManager.disableAlpha();

        for (int i = 0; i < 3; ++i) {
            float alpha = 1.0F / (float) (i + 1);
            int width = this.width;
            int height = this.height;
            float u = (float) (i - 3 / 2) / 256.0F;
            worldRenderer.pos(width, height, this.zLevel).tex(u, 1.0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
            worldRenderer.pos(width, 0.0, this.zLevel).tex(u + 1.0F, 1.0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
            worldRenderer.pos(0.0, 0.0, this.zLevel).tex(u + 1.0F, 0.0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
            worldRenderer.pos(0.0, height, this.zLevel).tex(u, 0.0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableAlpha();
        GlStateManager.colorMask(true, true, true, true);
    }

    private void renderSkybox(float partialTicks) {
        this.mc.getFramebuffer().unbindFramebuffer();
        GlStateManager.viewport(0, 0, 256, 256);
        this.drawPanorama(partialTicks);
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        float f = this.width > this.height ? 120.0F / (float) this.width : 120.0F / (float) this.height;
        float u = (float) this.height * f / 256.0F;
        float v = (float) this.width * f / 256.0F;
        int width = this.width;
        int height = this.height;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0, height, this.zLevel).tex(0.5F - u, 0.5F + v).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(width, height, this.zLevel).tex(0.5F - u, 0.5F - v).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(width, 0.0, this.zLevel).tex(0.5F + u, 0.5F - v).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(0.0, 0.0, this.zLevel).tex(0.5F + u, 0.5F + v).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();
    }

    private void selectNewSplashText() {
        String prevSplash = splashText;
        if (prevSplash == null) {
            prevSplash = "";
        }

        do {
            splashText = GeneralUtil.randomElement(splashes);
        } while (prevSplash.equals(splashText));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DAY_OF_MONTH) == 24) {
            this.splashText = "Merry Christmas!";
        } else if (calendar.get(Calendar.MONTH) == Calendar.JANUARY && calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            this.splashText = "Happy new year!";
        } else if (calendar.get(Calendar.MONTH) == Calendar.OCTOBER && calendar.get(Calendar.DAY_OF_MONTH) == 31) {
            this.splashText = "OOoooOOOoooo! Spooky!";
        }
    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    private static void loadSplashes() {
        try {
            splashes = new ArrayList<>();
            IResource resource = Minecraft.getMinecraft()
                    .getResourceManager()
                    .getResource(new ResourceLocation("nonsense/splashes.txt"));
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                while (reader.ready()) {
                    splashes.add(reader.readLine());
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static class Button {
        public int x, y;
        private final String text;
        public final Runnable action;

        private Button(String text, Runnable action) {
            this.text = text;
            this.action = action;
        }

        public void draw(int mouseX, int mouseY) {
            boolean intersecting = this.mouseIntersecting(mouseX, mouseY);
            NVGHelper.translate(this.x, this.y);
            NVGHelper.drawRoundedRect(-100, -20, 200, 40, 8, intersecting ? 0xAA000000 : 0x55000000);
            NVGHelper.fontFace(Fonts.ARIAL);
            NVGHelper.fontSize(22.0F);
            NVGHelper.textAlign(NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
            NVGHelper.drawText(this.text, 0.0F, 0.0F, intersecting ? ColorUtil.NONSENSE : 0xFFFFFFFF, true);
            NVGHelper.resetTransform();
        }

        private boolean mouseIntersecting(int mouseX, int mouseY) {
            return InputUtil.mouseIntersecting(mouseX, mouseY, this.x - 100, this.y - 20, 200, 40);
        }
    }

    public class IconButton {
        public static final int SIZE = 64;

        public int x, y;
        public final ResourceLocation resource;
        public final Runnable action;

        public IconButton(String resource, Runnable action) {
            this.resource = new ResourceLocation(resource);
            this.action = action;
            this.x = 0;
            this.y = 0;
        }

        public void draw(int mouseX, int mouseY) {
            NonsenseMainMenu.this.mc.getTextureManager().bindTexture(this.resource);
            if (this.mouseIntersecting(mouseX, mouseY)) {
                GlStateManager.color(1.0F, 1.0F / 3.0F, 1.0F / 3.0F);
            } else {
                GlStateManager.color(1.0F, 1.0F, 1.0F);
            }
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
            Gui.drawModalRectWithCustomSizedTexture(this.x, this.y, 0.0F, 0.0F, SIZE, SIZE, SIZE, SIZE);
            GlStateManager.disableBlend();
        }

        public boolean mouseIntersecting(int mouseX, int mouseY) {
            return InputUtil.mouseIntersecting(mouseX, mouseY, this.x, this.y, SIZE, SIZE);
        }

    }
}
