package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL13;

import optifine.Config;

import static org.lwjgl.opengl.GL11.*;

public class GlStateManager {
    private static final GlStateManager.AlphaState alphaState = new GlStateManager.AlphaState(null);
    private static final GlStateManager.BooleanState lightingState = new GlStateManager.BooleanState(GL_LIGHTING);
    private static final GlStateManager.BooleanState[] lightState = new GlStateManager.BooleanState[8];
    private static final GlStateManager.ColorMaterialState colorMaterialState = new GlStateManager.ColorMaterialState(null);
    private static final GlStateManager.BlendState blendState = new GlStateManager.BlendState(null);
    private static final GlStateManager.DepthState depthState = new GlStateManager.DepthState(null);
    private static final GlStateManager.FogState fogState = new GlStateManager.FogState(null);
    private static final GlStateManager.CullState cullState = new GlStateManager.CullState(null);
    private static final GlStateManager.PolygonOffsetState polygonOffsetState = new GlStateManager.PolygonOffsetState(null);
    private static final GlStateManager.ColorLogicState colorLogicState = new GlStateManager.ColorLogicState(null);
    private static final GlStateManager.TexGenState texGenState = new GlStateManager.TexGenState(null);
    private static final GlStateManager.ClearState clearState = new GlStateManager.ClearState(null);
    private static final GlStateManager.StencilState stencilState = new GlStateManager.StencilState(null);
    private static final GlStateManager.BooleanState normalizeState = new GlStateManager.BooleanState(GL_NORMALIZE);
    private static int activeTextureUnit = 0;
    private static final GlStateManager.TextureState[] textureState = new GlStateManager.TextureState[32];
    private static int activeShadeModel = GL_SMOOTH;
    private static final GlStateManager.BooleanState rescaleNormalState = new GlStateManager.BooleanState(GL13.GL_RESCALE_NORMAL);
    private static final GlStateManager.ColorMask colorMaskState = new GlStateManager.ColorMask(null);
    private static final GlStateManager.Color colorState = new GlStateManager.Color();
    public static boolean clearEnabled = true;

    public static void pushAttrib() {
        glPushAttrib(GL_ENABLE_BIT | GL_LIGHTING_BIT);
    }

    public static void popAttrib() {
        glPopAttrib();
    }

    public static void disableAlpha() {
        alphaState.state.setDisabled();
    }

    public static void enableAlpha() {
        alphaState.state.setEnabled();
    }

    public static void alphaFunc(int func, float ref) {
        if (func != alphaState.func || ref != alphaState.ref) {
            alphaState.func = func;
            alphaState.ref = ref;
            glAlphaFunc(func, ref);
        }
    }

    public static void enableLighting() {
        lightingState.setEnabled();
    }

    public static void disableLighting() {
        lightingState.setDisabled();
    }

    public static void enableLight(int light) {
        lightState[light].setEnabled();
    }

    public static void disableLight(int light) {
        lightState[light].setDisabled();
    }

    public static void enableColorMaterial() {
        colorMaterialState.state.setEnabled();
    }

    public static void disableColorMaterial() {
        colorMaterialState.state.setDisabled();
    }

    public static void colorMaterial(int face, int mode) {
        if (face != colorMaterialState.face || mode != colorMaterialState.mode) {
            colorMaterialState.face = face;
            colorMaterialState.mode = mode;
            glColorMaterial(face, mode);
        }
    }

    public static void disableDepth() {
        depthState.depthTest.setDisabled();
    }

    public static void enableDepth() {
        depthState.depthTest.setEnabled();
    }

    public static void depthFunc(int depthFunc) {
        if (depthFunc != depthState.depthFunc) {
            depthState.depthFunc = depthFunc;
            glDepthFunc(depthFunc);
        }
    }

    public static void depthMask(boolean flagIn) {
        if (flagIn != depthState.maskEnabled) {
            depthState.maskEnabled = flagIn;
            glDepthMask(flagIn);
        }
    }

    public static void disableBlend() {
        blendState.state.setDisabled();
    }

    public static void enableBlend() {
        blendState.state.setEnabled();
    }

    public static void blendFunc(int srcFactor, int dstFactor) {
        if (srcFactor != blendState.srcFactor || dstFactor != blendState.dstFactor) {
            blendState.srcFactor = srcFactor;
            blendState.dstFactor = dstFactor;
            glBlendFunc(srcFactor, dstFactor);
        }
    }

    public static void tryBlendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
        if (srcFactor != blendState.srcFactor || dstFactor != blendState.dstFactor || srcFactorAlpha != blendState.srcFactorAlpha || dstFactorAlpha != blendState.dstFactorAlpha) {
            blendState.srcFactor = srcFactor;
            blendState.dstFactor = dstFactor;
            blendState.srcFactorAlpha = srcFactorAlpha;
            blendState.dstFactorAlpha = dstFactorAlpha;
            OpenGlHelper.glBlendFunc(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
        }
    }

    public static void enableFog() {
        fogState.state.setEnabled();
    }

    public static void disableFog() {
        fogState.state.setDisabled();
    }

    public static void setFog(int param) {
        if (param != fogState.mode) {
            fogState.mode = param;
            glFogi(GL_FOG_MODE, param);
        }
    }

    public static void setFogDensity(float param) {
        if (param != fogState.density) {
            fogState.density = param;
            glFogf(GL_FOG_DENSITY, param);
        }
    }

    public static void setFogStart(float param) {
        if (param != fogState.start) {
            fogState.start = param;
            glFogf(GL_FOG_START, param);
        }
    }

    public static void setFogEnd(float param) {
        if (param != fogState.end) {
            fogState.end = param;
            glFogf(GL_FOG_END, param);
        }
    }

    public static void enableCull() {
        cullState.state.setEnabled();
    }

    public static void disableCull() {
        cullState.state.setDisabled();
    }

    public static void cullFace(int mode) {
        if (mode != cullState.mode) {
            cullState.mode = mode;
            glCullFace(mode);
        }
    }

    public static void enablePolygonOffset() {
        polygonOffsetState.fill.setEnabled();
    }

    public static void disablePolygonOffset() {
        polygonOffsetState.fill.setDisabled();
    }

    public static void doPolygonOffset(float factor, float units) {
        if (factor != polygonOffsetState.factor || units != polygonOffsetState.units) {
            polygonOffsetState.factor = factor;
            polygonOffsetState.units = units;
            glPolygonOffset(factor, units);
        }
    }

    public static void enableColorLogic() {
        colorLogicState.state.setEnabled();
    }

    public static void disableColorLogic() {
        colorLogicState.state.setDisabled();
    }

    public static void colorLogicOp(int opcode) {
        if (opcode != colorLogicState.opcode) {
            colorLogicState.opcode = opcode;
            glLogicOp(opcode);
        }
    }

    public static void enableTexGenCoord(GlStateManager.TexGen texGen) {
        texGenCoord(texGen).state.setEnabled();
    }

    public static void disableTexGenCoord(GlStateManager.TexGen texGen) {
        texGenCoord(texGen).state.setDisabled();
    }

    public static void texGen(GlStateManager.TexGen p_179149_0_, int p_179149_1_) {
        GlStateManager.TexGenCoord glstatemanager$texgencoord = texGenCoord(p_179149_0_);

        if (p_179149_1_ != glstatemanager$texgencoord.field_179066_c) {
            glstatemanager$texgencoord.field_179066_c = p_179149_1_;
            glTexGeni(glstatemanager$texgencoord.mode, GL_TEXTURE_GEN_MODE, p_179149_1_);
        }
    }

    public static void func_179105_a(GlStateManager.TexGen p_179105_0_, int pname, FloatBuffer params) {
        glTexGenfv(texGenCoord(p_179105_0_).mode, pname, params);
    }

    private static GlStateManager.TexGenCoord texGenCoord(GlStateManager.TexGen texGen) {
        return switch (GlStateManager$1.INDEX2GEN[texGen.ordinal()]) {
            case 2 -> texGenState.t;
            case 3 -> texGenState.r;
            case 4 -> texGenState.q;
            default -> texGenState.s;
        };
    }

    public static void setActiveTexture(int texture) {
        if (activeTextureUnit != texture - OpenGlHelper.defaultTexUnit) {
            activeTextureUnit = texture - OpenGlHelper.defaultTexUnit;
            OpenGlHelper.setActiveTexture(texture);
        }
    }

    public static void enableTexture2D() {
        textureState[activeTextureUnit].texture2DState.setEnabled();
    }

    public static void disableTexture2D() {
        textureState[activeTextureUnit].texture2DState.setDisabled();
    }

    public static int generateTexture() {
        return glGenTextures();
    }

    public static void deleteTexture(int texture) {
        if (texture != 0) {
            glDeleteTextures(texture);

            for (GlStateManager.TextureState textureState : textureState) {
                if (textureState.textureName == texture) {
                    textureState.textureName = 0;
                }
            }
        }
    }

    public static void bindTexture(int texture) {
        if (texture != textureState[activeTextureUnit].textureName) {
            textureState[activeTextureUnit].textureName = texture;
            glBindTexture(GL_TEXTURE_2D, texture);
        }
    }

    public static void bindCurrentTexture() {
        glBindTexture(GL_TEXTURE_2D, textureState[activeTextureUnit].textureName);
    }

    public static void enableNormalize() {
        normalizeState.setEnabled();
    }

    public static void disableNormalize() {
        normalizeState.setDisabled();
    }

    public static void shadeModel(int mode) {
        if (mode != activeShadeModel) {
            activeShadeModel = mode;
            glShadeModel(mode);
        }
    }

    public static void enableRescaleNormal() {
        rescaleNormalState.setEnabled();
    }

    public static void disableRescaleNormal() {
        rescaleNormalState.setDisabled();
    }

    public static void viewport(int x, int y, int width, int height) {
        glViewport(x, y, width, height);
    }

    public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        if (red != colorMaskState.red || green != colorMaskState.green || blue != colorMaskState.blue || alpha != colorMaskState.alpha) {
            colorMaskState.red = red;
            colorMaskState.green = green;
            colorMaskState.blue = blue;
            colorMaskState.alpha = alpha;
            glColorMask(red, green, blue, alpha);
        }
    }

    public static void clearDepth(double depth) {
        if (depth != clearState.depth) {
            clearState.depth = depth;
            glClearDepth(depth);
        }
    }

    public static void clearColor(float red, float green, float blue, float alpha) {
        if (red != clearState.color.red || green != clearState.color.green || blue != clearState.color.blue || alpha != clearState.color.alpha) {
            clearState.color.red = red;
            clearState.color.green = green;
            clearState.color.blue = blue;
            clearState.color.alpha = alpha;
            glClearColor(red, green, blue, alpha);
        }
    }

    public static void clear(int mask) {
        if (clearEnabled) {
            glClear(mask);
        }
    }

    public static void matrixMode(int mode) {
        glMatrixMode(mode);
    }

    public static void loadIdentity() {
        glLoadIdentity();
    }

    public static void pushMatrix() {
        glPushMatrix();
    }

    public static void popMatrix() {
        glPopMatrix();
    }

    public static void getFloat(int pname, FloatBuffer params) {
        glGetFloatv(pname, params);
    }

    public static void ortho(double left, double right, double bottom, double top, double zNear, double zFar) {
        glOrtho(left, right, bottom, top, zNear, zFar);
    }

    public static void rotate(float angle, float x, float y, float z) {
        glRotatef(angle, x, y, z);
    }

    public static void scale(float x, float y, float z) {
        glScalef(x, y, z);
    }

    public static void scale(double x, double y, double z) {
        glScaled(x, y, z);
    }

    public static void translate(float x, float y, float z) {
        glTranslatef(x, y, z);
    }

    public static void translate(double x, double y, double z) {
        glTranslated(x, y, z);
    }

    public static void multMatrix(FloatBuffer matrix) {
        glMultMatrixf(matrix);
    }

    public static void color(float colorRed, float colorGreen, float colorBlue, float colorAlpha) {
        if (colorRed != colorState.red || colorGreen != colorState.green || colorBlue != colorState.blue || colorAlpha != colorState.alpha) {
            colorState.red = colorRed;
            colorState.green = colorGreen;
            colorState.blue = colorBlue;
            colorState.alpha = colorAlpha;
            glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
        }
    }

    public static void color(float colorRed, float colorGreen, float colorBlue) {
        color(colorRed, colorGreen, colorBlue, 1.0F);
    }

    public static void resetColor() {
        colorState.red = colorState.green = colorState.blue = colorState.alpha = -1.0F;
    }

    public static void callList(int list) {
        glCallList(list);
    }

    public static int getActiveTextureUnit() {
        return OpenGlHelper.defaultTexUnit + activeTextureUnit;
    }

    public static int getBoundTexture() {
        return textureState[activeTextureUnit].textureName;
    }

    public static void checkBoundTexture() {
        if (Config.isMinecraftThread()) {
            int i = glGetInteger(GL13.GL_ACTIVE_TEXTURE);
            int j = glGetInteger(GL_TEXTURE_BINDING_2D);
            int k = getActiveTextureUnit();
            int l = getBoundTexture();

            if (l > 0) {
                if (i != k || j != l) {
                    Config.dbg("checkTexture: act: " + k + ", glAct: " + i + ", tex: " + l + ", glTex: " + j);
                }
            }
        }
    }

    public static void deleteTextures(IntBuffer p_deleteTextures_0_) {
        p_deleteTextures_0_.rewind();

        while (p_deleteTextures_0_.position() < p_deleteTextures_0_.limit()) {
            int i = p_deleteTextures_0_.get();
            deleteTexture(i);
        }

        p_deleteTextures_0_.rewind();
    }

    static {
        for (int i = 0; i < 8; ++i) {
            lightState[i] = new GlStateManager.BooleanState(GL_LIGHT0 + i);
        }

        for (int j = 0; j < textureState.length; ++j) {
            textureState[j] = new GlStateManager.TextureState(null);
        }
    }

    static final class GlStateManager$1 {
        static final int[] INDEX2GEN = new int[GlStateManager.TexGen.values().length];

        static {
            try {
                INDEX2GEN[GlStateManager.TexGen.S.ordinal()] = 1;
            } catch (NoSuchFieldError _) {
            }

            try {
                INDEX2GEN[GlStateManager.TexGen.T.ordinal()] = 2;
            } catch (NoSuchFieldError _) {
            }

            try {
                INDEX2GEN[GlStateManager.TexGen.R.ordinal()] = 3;
            } catch (NoSuchFieldError _) {
            }

            try {
                INDEX2GEN[GlStateManager.TexGen.Q.ordinal()] = 4;
            } catch (NoSuchFieldError _) {
            }
        }
    }

    static class AlphaState {
        public GlStateManager.BooleanState state;
        public int func;
        public float ref;

        private AlphaState() {
            this.state = new GlStateManager.BooleanState(3008);
            this.func = 519;
            this.ref = -1.0F;
        }

        AlphaState(GlStateManager.GlStateManager$1 p_i46489_1_) {
            this();
        }
    }

    static class BlendState {
        public GlStateManager.BooleanState state;
        public int srcFactor;
        public int dstFactor;
        public int srcFactorAlpha;
        public int dstFactorAlpha;

        private BlendState() {
            this.state = new GlStateManager.BooleanState(3042);
            this.srcFactor = 1;
            this.dstFactor = 0;
            this.srcFactorAlpha = 1;
            this.dstFactorAlpha = 0;
        }

        BlendState(GlStateManager.GlStateManager$1 p_i46488_1_) {
            this();
        }
    }

    static class BooleanState {
        private final int capability;
        private boolean currentState = false;

        public BooleanState(int capabilityIn) {
            this.capability = capabilityIn;
        }

        public void setDisabled() {
            this.setState(false);
        }

        public void setEnabled() {
            this.setState(true);
        }

        public void setState(boolean state) {
            if (state != this.currentState) {
                this.currentState = state;

                if (state) {
                    glEnable(this.capability);
                } else {
                    glDisable(this.capability);
                }
            }
        }
    }

    static class ClearState {
        public double depth;
        public GlStateManager.Color color;
        public int field_179204_c;

        private ClearState() {
            this.depth = 1.0D;
            this.color = new GlStateManager.Color(0.0F, 0.0F, 0.0F, 0.0F);
            this.field_179204_c = 0;
        }

        ClearState(GlStateManager.GlStateManager$1 p_i46487_1_) {
            this();
        }
    }

    static class Color {
        public float red = 1.0F;
        public float green = 1.0F;
        public float blue = 1.0F;
        public float alpha = 1.0F;

        public Color() {
        }

        public Color(float redIn, float greenIn, float blueIn, float alphaIn) {
            this.red = redIn;
            this.green = greenIn;
            this.blue = blueIn;
            this.alpha = alphaIn;
        }
    }

    static class ColorLogicState {
        public GlStateManager.BooleanState state;
        public int opcode;

        private ColorLogicState() {
            this.state = new GlStateManager.BooleanState(GL_COLOR_LOGIC_OP);
            this.opcode = GL_COPY;
        }

        ColorLogicState(GlStateManager.GlStateManager$1 p_i46486_1_) {
            this();
        }
    }

    static class ColorMask {
        public boolean red;
        public boolean green;
        public boolean blue;
        public boolean alpha;

        private ColorMask() {
            this.red = true;
            this.green = true;
            this.blue = true;
            this.alpha = true;
        }

        ColorMask(GlStateManager.GlStateManager$1 p_i46485_1_) {
            this();
        }
    }

    static class ColorMaterialState {
        public GlStateManager.BooleanState state;
        public int face;
        public int mode;

        private ColorMaterialState() {
            this.state = new GlStateManager.BooleanState(2903);
            this.face = 1032;
            this.mode = 5634;
        }

        ColorMaterialState(GlStateManager.GlStateManager$1 p_i46484_1_) {
            this();
        }
    }

    static class CullState {
        public GlStateManager.BooleanState state;
        public int mode;

        private CullState() {
            this.state = new GlStateManager.BooleanState(2884);
            this.mode = 1029;
        }

        CullState(GlStateManager.GlStateManager$1 p_i46483_1_) {
            this();
        }
    }

    static class DepthState {
        public GlStateManager.BooleanState depthTest;
        public boolean maskEnabled;
        public int depthFunc;

        private DepthState() {
            this.depthTest = new GlStateManager.BooleanState(2929);
            this.maskEnabled = true;
            this.depthFunc = 513;
        }

        DepthState(GlStateManager.GlStateManager$1 p_i46482_1_) {
            this();
        }
    }

    static class FogState {
        public GlStateManager.BooleanState state;
        public int mode;
        public float density;
        public float start;
        public float end;

        private FogState() {
            this.state = new GlStateManager.BooleanState(2912);
            this.mode = 2048;
            this.density = 1.0F;
            this.start = 0.0F;
            this.end = 1.0F;
        }

        FogState(GlStateManager.GlStateManager$1 p_i46481_1_) {
            this();
        }
    }

    static class PolygonOffsetState {
        public GlStateManager.BooleanState fill;
        public GlStateManager.BooleanState line;
        public float factor;
        public float units;

        private PolygonOffsetState() {
            this.fill = new GlStateManager.BooleanState(GL_POLYGON_OFFSET_FILL);
            this.line = new GlStateManager.BooleanState(GL_POLYGON_OFFSET_LINE);
            this.factor = 0.0F;
            this.units = 0.0F;
        }

        PolygonOffsetState(GlStateManager.GlStateManager$1 p_i46480_1_) {
            this();
        }
    }

    static class StencilFunc {
        public int func;
        public int ref;
        public int mask;

        private StencilFunc() {
            this.func = GL_ALWAYS;
            this.ref = 0;
            this.mask = -1;
        }

        StencilFunc(GlStateManager.GlStateManager$1 p_i46479_1_) {
            this();
        }
    }

    static class StencilState {
        public GlStateManager.StencilFunc stencilFunc;
        public int field_179076_b;
        public int field_179077_c;
        public int field_179074_d;
        public int field_179075_e;

        private StencilState() {
            this.stencilFunc = new GlStateManager.StencilFunc(null);
            this.field_179076_b = -1;
            this.field_179077_c = GL_KEEP;
            this.field_179074_d = GL_KEEP;
            this.field_179075_e = GL_KEEP;
        }

        StencilState(GlStateManager.GlStateManager$1 p_i46478_1_) {
            this();
        }
    }

    public enum TexGen {
        S, T, R, Q
    }

    static class TexGenCoord {
        public GlStateManager.BooleanState state;
        public int mode;
        public int field_179066_c = -1;

        public TexGenCoord(int mode, int state) {
            this.mode = mode;
            this.state = new GlStateManager.BooleanState(state);
        }
    }

    static class TexGenState {
        public GlStateManager.TexGenCoord s;
        public GlStateManager.TexGenCoord t;
        public GlStateManager.TexGenCoord r;
        public GlStateManager.TexGenCoord q;

        private TexGenState() {
            this.s = new GlStateManager.TexGenCoord(GL_S, GL_TEXTURE_GEN_S);
            this.t = new GlStateManager.TexGenCoord(GL_T, GL_TEXTURE_GEN_T);
            this.r = new GlStateManager.TexGenCoord(GL_R, GL_TEXTURE_GEN_R);
            this.q = new GlStateManager.TexGenCoord(GL_Q, GL_TEXTURE_GEN_Q);
        }

        TexGenState(GlStateManager.GlStateManager$1 p_i46477_1_) {
            this();
        }
    }

    static class TextureState {
        public GlStateManager.BooleanState texture2DState;
        public int textureName;

        private TextureState() {
            this.texture2DState = new GlStateManager.BooleanState(3553);
            this.textureName = 0;
        }

        TextureState(GlStateManager.GlStateManager$1 p_i46476_1_) {
            this();
        }
    }
}
