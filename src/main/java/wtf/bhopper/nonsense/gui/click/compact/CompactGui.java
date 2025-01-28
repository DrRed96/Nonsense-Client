package wtf.bhopper.nonsense.gui.click.compact;

import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.Display;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.util.misc.InputUtil;
import wtf.bhopper.nonsense.util.misc.Vec2i;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.Fonts;
import wtf.bhopper.nonsense.util.render.NVGHelper;
import wtf.bhopper.nonsense.util.render.Translate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompactGui extends GuiScreen {

    public static final int CATEGORY_SIZE = 80;
    public static final int MODULES_SIZE = 240;
    public static final int TOTAL_SIZE = CATEGORY_SIZE * ModuleCategory.values().length;
    public static final int MODULE_FONT_SIZE = 20;

    public int x = 50;
    public int y = 50;

    private ModuleCategory selectedCategory = ModuleCategory.COMBAT;
    private final Translate categoryTranslate = new Translate(0.0F, 0.0F);

    private final Map<ModuleCategory, List<CompactModule>> modules = new HashMap<>();

    public CompactModule expandedModule = null;

    public CompactGui() {
        for (ModuleCategory category : ModuleCategory.values()) {
            List<Module> modulesInCategory = Nonsense.getModuleManager()
                    .getInCategory(category);

            List<CompactModule> compactModules = new ArrayList<>();

            int count = 0;
            for (Module module : modulesInCategory) {
                compactModules.add(new CompactModule(this, module, 4 + count * (MODULE_FONT_SIZE + 8)));
                ++count;
            }

            this.modules.put(category, compactModules);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float delta) {

        Vec2i mousePosOrig = InputUtil.getUnscaledMousePositions().add(-this.x, -this.y);

        NVGHelper.begin();

        NVGHelper.translate(this.x, this.y);
        this.drawCategories(delta);
        this.drawMainPanel(mousePosOrig.x + CATEGORY_SIZE, mousePosOrig.y, delta);

        if (this.expandedModule != null) {
            this.drawModule(mousePosOrig.x + CATEGORY_SIZE + MODULES_SIZE, mousePosOrig.y, delta);
        }

        NVGHelper.end();
    }

    private void drawCategories(float delta) {

        this.categoryTranslate.interpolate(0.0F, selectedCategory.ordinal() * CATEGORY_SIZE, 0.2F, delta);

        NVGHelper.drawRect(0.0F, 0.0F, CATEGORY_SIZE, TOTAL_SIZE, 0xFF222222);
        Hud.rect(0.0F, this.categoryTranslate.getY(), CATEGORY_SIZE, CATEGORY_SIZE);

        NVGHelper.fontFace(Fonts.SKEET);
        NVGHelper.fontSize(48);
        NVGHelper.textAlign(NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_MIDDLE);
        for (ModuleCategory category : ModuleCategory.values()) {
            String icon = switch (category) {
                case COMBAT -> "E";
                case MOVEMENT -> "J";
                case PLAYER -> "F";
                case EXPLOIT -> "A";
                case OTHER -> "I";
                case VISUAL -> "C";
                case SCRIPT -> "H";
            };
            NVGHelper.drawText(icon, CATEGORY_SIZE / 2.0F, category.ordinal() * CATEGORY_SIZE + CATEGORY_SIZE / 2.0F, this.selectedCategory == category ? ColorUtil.WHITE : ColorUtil.GRAY);
        }
    }

    private void drawMainPanel(int mouseX, int mouseY, float delta) {
        NVGHelper.translate(CATEGORY_SIZE, 0.0F);
        NVGHelper.drawRect(0.0F, 0.0F, MODULES_SIZE, TOTAL_SIZE, 0xFF171717);

        NVGHelper.scissor(0.0F, 0.0F, Display.getWidth(), TOTAL_SIZE);

        for (CompactModule module : this.modules.get(this.selectedCategory)) {
            module.draw(mouseX, mouseY + module.getY(), delta);
        }
    }

    private void drawModule(int mouseX, int mouseY, float delta) {
        NVGHelper.translate(MODULES_SIZE, 0.0F);
        NVGHelper.drawRect(0.0F, 0.0F, MODULES_SIZE, TOTAL_SIZE, 0xFF111111);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        Vec2i mousePos = InputUtil.getUnscaledMousePositions().add(-this.x, -this.y);

        if (mouseButton == 0) {
            for (ModuleCategory category : ModuleCategory.values()) {
                if (InputUtil.mouseIntersecting(mousePos.x, mousePos.y, 0.0F, category.ordinal() * CATEGORY_SIZE, CATEGORY_SIZE, CATEGORY_SIZE)) {
                    this.selectedCategory = category;
                }
            }
        }

        for (CompactModule compactModule : this.modules.get(this.selectedCategory)) {
            compactModule.onClick(mousePos.x - CATEGORY_SIZE, mousePos.y, mouseButton);
        }

    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
