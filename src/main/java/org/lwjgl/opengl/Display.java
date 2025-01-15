package org.lwjgl.opengl;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.glfw.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;

public class Display {

    private static String title = "";

    private static long handle = MemoryUtil.NULL;

    private static boolean resizable = false;

    private static DisplayMode displayMode = new DisplayMode(640, 480, 24, 60);

    private static int width = 0;
    private static int height = 0;

    private static int xPos = 0;
    private static int yPos = 0;

    private static boolean windowResized = false;
    private static GLFWWindowSizeCallback sizeCallback = null;

    private static ByteBuffer[] cachedIcons = null;

    public static long getHandle() {
        return handle;
    }

    public static DisplayMode getDesktopDisplayMode() {
        try {
            return Arrays.stream(getAvailableDisplayModes())
                    .max(Comparator.comparingInt(value -> value.getWidth() * value.getHeight()))
                    .orElse(null);
        } catch (LWJGLException _) {
            return null;
        }
    }

    public static int setIcon(ByteBuffer[] icons) {

        if (cachedIcons != icons) {
            cachedIcons = Arrays.stream(icons).map(Display::cloneByteBuffer).toArray(ByteBuffer[]::new);
        }

        if (isCreated()) {
            GLFW.glfwSetWindowIcon(handle, iconsToGLFWBuffer(cachedIcons));
            return 1;
        }

        return 0;
    }

    public static void update() {
        windowResized = false;
        GLFW.glfwPollEvents();

        if (Mouse.isCreated()) {
            Mouse.poll();
        }

        if (Keyboard.isCreated()) {
            Keyboard.poll();
        }

        GLFW.glfwSwapBuffers(handle);
    }

    public static void create(PixelFormat pixelFormat) throws LWJGLException {
        create();
    }

    public static void create() throws LWJGLException {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new LWJGLException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);

        handle = GLFW.glfwCreateWindow(displayMode.getWidth(), displayMode.getHeight(), title, MemoryUtil.NULL, MemoryUtil.NULL);

        width = displayMode.getWidth();
        height = displayMode.getHeight();

        GLFW.glfwMakeContextCurrent(handle);
        GL.createCapabilities();

        sizeCallback = GLFWWindowSizeCallback.create(Display::resizeCallback);
        GLFW.glfwSetWindowSizeCallback(handle, sizeCallback);

        Mouse.create();
        Keyboard.create();

        GLFW.glfwShowWindow(handle);

        if (cachedIcons != null) {
            setIcon(cachedIcons);
        }

    }

    public static void setFullscreen(boolean fullscreen) {

        try {

            resizeCallback(handle, displayMode.getWidth(), displayMode.getHeight());

            if (fullscreen) {
                long monitor = GLFW.glfwGetPrimaryMonitor();
                GLFW.glfwSetWindowMonitor(handle, monitor, 0, 0, width, height, displayMode.getFrequency());
                xPos = displayMode.getWidth() / 2;
                yPos = displayMode.getHeight() / 2;
            } else {
                xPos -= width / 2;
                yPos -= height / 2;
                GLFW.glfwSetWindowMonitor(handle, MemoryUtil.NULL, xPos, yPos, width, height, -1);
            }

            GLFW.glfwSetWindowSize(handle, width, height);

        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public static DisplayMode[] getAvailableDisplayModes() throws LWJGLException {
        long primaryMonitor = GLFW.glfwGetPrimaryMonitor();
        if (primaryMonitor == MemoryUtil.NULL) {
            return new DisplayMode[0];
        }

        GLFWVidMode.Buffer videoModes = GLFW.glfwGetVideoModes(primaryMonitor);

        if (videoModes == null) {
            throw new LWJGLException("No video modes found");
        }

        return videoModes.stream()
                .map(mode -> new DisplayMode(mode.width(), mode.height(), mode.redBits() + mode.greenBits() + mode.blueBits(), mode.refreshRate())).distinct()
                .toArray(DisplayMode[]::new);
    }

    private static void resizeCallback(long window, int width, int height) {
        if (window == handle) {
            windowResized = true;
            Display.width = width;
            Display.height = height;
        }
    }

    public static void destroyWindow() {
        sizeCallback.free();
        Mouse.destroy();
        Keyboard.destroy();
        GLFW.glfwDestroyWindow(handle);
    }

    public static void destroy() {
        destroyWindow();
        GLFW.glfwTerminate();
        GLFWErrorCallback cb = GLFW.glfwSetErrorCallback(null);
        if (cb != null) {
            cb.free();
        }
    }

    public static boolean isCreated() {
        return handle != MemoryUtil.NULL;
    }

    public static boolean isCloseRequested() {
        return GLFW.glfwWindowShouldClose(handle);
    }

    public static boolean isActive() {
        return true;
    }

    public static void setResizable(boolean isResizable) {
        resizable = isResizable;
        if (isCreated()) {
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        }
    }

    public static void sync(int fps) {
        Sync.sync(fps);
    }

    public static void setVSyncEnabled(boolean enabled) {
        GLFW.glfwSwapInterval(enabled ? 1 : 0);
    }

    public static String getTitle() {
        return title;
    }

    public static void setTitle(String title) {
        Display.title = title;
        if (isCreated()) {
            GLFW.glfwSetWindowTitle(handle, title);
        }
    }


    public static boolean wasResized() {
        return windowResized;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static DisplayMode getDisplayMode() {
        return displayMode;
    }

    public static void setDisplayMode(DisplayMode displayMode) {
        Display.displayMode = displayMode;
    }

    private static ByteBuffer cloneByteBuffer(ByteBuffer original) {

        ByteBuffer clone = BufferUtils.createByteBuffer(original.capacity());
        int oldPosition = original.position();
        clone.put(original);

        original.position(oldPosition);
        clone.flip();

        return clone;
    }

    private static GLFWImage.Buffer iconsToGLFWBuffer(ByteBuffer[] icons) {
        GLFWImage.Buffer buffer = GLFWImage.create(icons.length);

        for (ByteBuffer icon : icons) {
            int size = icon.limit() / 4;
            int dimenstion = (int)Math.sqrt(size);
            try (GLFWImage image = GLFWImage.malloc()) {
                buffer.put(image.set(dimenstion, dimenstion, icon));
            }
        }

        buffer.flip();
        return buffer;
    }

}
