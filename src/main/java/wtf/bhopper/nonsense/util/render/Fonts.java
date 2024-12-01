package wtf.bhopper.nonsense.util.render;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;

import static org.lwjgl.nanovg.NanoVG.nvgCreateFont;

public enum Fonts implements MinecraftInstance {
    ARIAL("arial", "arial.ttf"),
    CASCADIA_MONO("cascadiamono", "CascadiaMono.ttf"),
    COMIC_SANS("comicsans", "comic.ttf"),
    CONSOLAS("consolas", "consola.ttf"),
    JETBRAINS_MONO("jetbrainsmono", "JetBrainsMono-Regular.ttf"),
    ROBOTO("roboto", "Roboto-Regular.ttf"),
    SANS_SERIF("sansserif", "micross.ttf"),
    SEGOE("segoe","segoeui.ttf"),
    SEGOE_BOLD("segoebold", "segoeuib.ttf"),
    TAHOMA("tahoma", "tahoma.ttf"), // Same font that Exhibition uses
    TIMES_NEW_ROMAN("timesnewroman", "times.ttf");

    public final String name;
    private final ResourceLocation location;
    private int vgFont;

    Fonts(String name, String resource) {
        this.name = name;
        this.location = new ResourceLocation("nonsense/fonts/" + resource);
    }

    public int getVgFont() {
        return vgFont;
    }

    public static void init() {
        for (Fonts font : values()) {
            String path = GeneralUtil.getResourcePathString(font.location);
            font.vgFont = nvgCreateFont(NVGHelper.ctx(), font.name, path);

            if (font.vgFont == -1) {
                throw new RuntimeException("Failed to load font: " + path);
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
