package wtf.bhopper.nonsense.util.render;

import net.minecraft.util.ResourceLocation;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;

import static org.lwjgl.nanovg.NanoVG.nvgCreateFont;

public enum Fonts implements MinecraftInstance {
    ARIAL("arial", "nonsense/fonts/arial.ttf"),
    SEGOE("segoeui","nonsense/fonts/segoeui.ttf"),
    SEGOE_BOLD("segoeuib", "nonsense/fonts/segoeuib.ttf")
    ;

    public final String name;
    private final ResourceLocation location;
    private int vgFont;

    Fonts(String name, String resource) {
        this.name = name;
        this.location = new ResourceLocation(resource);
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

}
