package wtf.bhopper.nonsense.util.render;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.misc.ResourceUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import static org.lwjgl.nanovg.NanoVG.nvgCreateFont;
import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;

public enum Fonts implements MinecraftInstance {
    ARIAL("arial", "arial.ttf"),
    CASCADIA_MONO("cascadiamono", "CascadiaMono.ttf"),
    COMIC_SANS("comicsans", "comic.ttf"),
    CONSOLAS("consolas", "consola.ttf"),
    HELVETICA("helvetica", "helveticaregular.ttf"),
    HELVETICA_LIGHT("helveticalight", "helveticalight.ttf"),
    HELVETICA_MEDIUM("helveticamedium", "helveticamedium.ttf"),
    JETBRAINS_MONO("jetbrainsmono", "JetBrainsMono-Regular.ttf"),
    OUTFIT("outfit", "outfit.ttf"),
    ROBOTO("roboto", "Roboto-Regular.ttf"),
    SANS_SERIF("sansserif", "micross.ttf"),
    SEGOE("segoe","segoeui.ttf"),
    SEGOE_BOLD("segoebold", "segoeuib.ttf"),
    SF_PRO_ROUNDED("sfprorounded", "SF-Pro-Rounded-Regular.otf"),
    TAHOMA("tahoma", "tahoma.ttf"), // Same font that Exhibition uses
    TIMES_NEW_ROMAN("timesnewroman", "times.ttf");

    public final String name;
    private final ResourceLocation location;
    private int vgFont;
    private ByteBuffer data;

    Fonts(String name, String resource) {
        this.name = name;
        this.location = new ResourceLocation("nonsense/fonts/" + resource);
    }

    public int getVgFont() {
        return vgFont;
    }

    public static void init() {
        for (Fonts font : values()) {
            try {
                font.data = ResourceUtil.loadResource(font.location);
                font.vgFont = NVGHelper.createFontMem(font.name, font.data, false);

                if (font.vgFont == -1) {
                    throw new RuntimeException("Failed to load font: " + font.name);
                }
            } catch (IOException | URISyntaxException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public static FontRenderer mc() {
        return mc.fontRendererObj;
    }

    public static FontRenderer bit() {
        return mc.bitFontRenderer;
    }

}
