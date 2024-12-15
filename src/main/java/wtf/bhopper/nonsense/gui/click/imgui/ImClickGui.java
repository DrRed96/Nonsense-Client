package wtf.bhopper.nonsense.gui.click.imgui;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImFloat;
import imgui.type.ImString;
import net.minecraft.client.gui.GuiScreen;
import org.lwjglx.input.Keyboard;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.impl.visual.ClickGui;
import wtf.bhopper.nonsense.module.property.Property;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.render.ImGuiHelper;

import java.util.ArrayList;
import java.util.List;

public class ImClickGui extends GuiScreen {

    private static final String NUMBER_FORMAT = "%.2f";

    private String search = "";
    private final ImString imSearch = new ImString(search, 1000);
    private final List<Module> searchModules = new ArrayList<>();
    private Module bindListener = null;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ImGuiHelper.newFrame();

        ImGui.setNextWindowSize(768, 600);
        if (ImGui.begin(Nonsense.NAME, ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse)) {
            if (ImGui.beginTabBar("#categories")) {

                // Module categories
                for (ModuleCategory category : ModuleCategory.values()) {
                    if (ImGui.beginTabItem(category.name)) {
                        this.drawModules(Nonsense.getModuleManager().getInCategory(category));
                        ImGui.endTabItem();
                    }

                }

                ImGui.endTabBar();

            }

            ImGui.end();
        }

        ImGuiHelper.endFrame();

    }

    private void drawModules(List<Module> modules) {

        boolean listenCheck = false;

        for (Module module : modules) {
            ImGui.pushID("module:" + module.name);

            if (ImGui.collapsingHeader(module.displayName)) {
                this.tooltip(module.description);

                // Enabled shit

                if (ImGui.checkbox("Toggled", module.isToggled())) {
                    module.toggle();
                }
                ImGui.sameLine();

                String keyStr = "Key-Bind: " + (this.bindListener == module ? "Listening..." : Keyboard.getKeyName(module.getBind()));
                if (ImGui.button(keyStr)) {
                    this.bindListener = module;
                }

                if (this.bindListener == module) {
                    listenCheck = true;
                }

                this.drawProperties(module.getProperties());

                ImGui.separator();

            } else {
                this.tooltip(module.description);
            }

            ImGui.popID();
        }

    }

    private void drawProperties(List<Property<?>> properties) {
        if (properties.isEmpty()) {
            return;
        }

        ImGui.separator();
        for (Property<?> property : properties) {
            switch (property) {
                case BooleanProperty booleanProperty -> this.drawBooleanProperty(booleanProperty);
                case NumberProperty numberProperty -> this.drawNumberProperty(numberProperty);
                case EnumProperty<?> enumProperty -> enumProperty.imGuiDraw();
                default -> {}
            }
        }

    }

    private void drawBooleanProperty(BooleanProperty property) {
        if (ImGui.checkbox(property.displayName, property.get())) {
            property.set(!property.get());
        }
        this.tooltip(property.description);
    }

    private void drawNumberProperty(NumberProperty property) {
        ImFloat v = new ImFloat(property.getFloat());
        if (ImGui.sliderFloat(property.displayName, v.getData(), (float)property.getMin(), (float)property.getMax(), NUMBER_FORMAT)) {
            property.set(v.doubleValue());
        }
        this.tooltip(property.description);
    }

    private void tooltip(String text) {
        if (ImGui.isItemHovered() && Nonsense.module(ClickGui.class).toolTips.get()) {
            ImGui.setTooltip(text);
        }
    }

    @Override
    public void onGuiClosed() {
        this.bindListener = null;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
