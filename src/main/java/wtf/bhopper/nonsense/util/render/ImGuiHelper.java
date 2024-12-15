package wtf.bhopper.nonsense.util.render;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjglx.opengl.Display;

public class ImGuiHelper {

    private static final String GLSL_VERSION = "#version 330";

    private static final ImGuiImplGlfw IMPL_GLFW = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 IMPL_GL3 = new ImGuiImplGl3();

    private static ImGuiIO io = null;

    public static void init(long handle) {
        ImGui.createContext();
        loadConfig();

        IMPL_GLFW.init(handle, true);
        IMPL_GL3.init(GLSL_VERSION);
    }

    public static void destroy() {
        IMPL_GL3.shutdown();
        IMPL_GLFW.shutdown();
        ImGui.destroyContext();
    }

    private static void loadConfig() {
        io = ImGui.getIO();
        io.setIniFilename(null);
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
    }

    public static void newFrame() {
        IMPL_GL3.newFrame();
        IMPL_GLFW.newFrame();
        ImGui.newFrame();
    }

    public static void endFrame() {
        ImGui.render();
        IMPL_GL3.renderDrawData(ImGui.getDrawData());
    }

    public static ImGuiImplGlfw glfw() {
        return IMPL_GLFW;
    }

    public static ImGuiIO io() {
        return io;
    }

}
