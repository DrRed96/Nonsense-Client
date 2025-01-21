package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import optifine.Config;
import optifine.Reflector;

import org.lwjgl.opengl.GL11;
import shadersmod.client.SVertexBuilder;

public class WorldVertexBufferUploader
{

    public void upload(WorldRenderer renderer)
    {
        if (renderer.getVertexCount() > 0)
        {
            VertexFormat vertexformat = renderer.getVertexFormat();
            int i = vertexformat.getNextOffset();
            ByteBuffer bytebuffer = renderer.getByteBuffer();
            List<VertexFormatElement> list = vertexformat.getElements();
            boolean flag = Reflector.ForgeVertexFormatElementEnumUseage_preDraw.exists();
            boolean flag1 = Reflector.ForgeVertexFormatElementEnumUseage_postDraw.exists();

            for (int j = 0; j < list.size(); ++j)
            {
                VertexFormatElement vertexformatelement = list.get(j);
                VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();

                if (flag)
                {
                    Reflector.callVoid(vertexformatelement$enumusage, Reflector.ForgeVertexFormatElementEnumUseage_preDraw, vertexformat, j, i, bytebuffer);
                }
                else
                {
                    int l = vertexformatelement.getType().getGlConstant();
                    int k = vertexformatelement.getIndex();
                    bytebuffer.position(vertexformat.func_181720_d(j));

                    switch (WorldVertexBufferUploader.WorldVertexBufferUploader$1.field_178958_a[vertexformatelement$enumusage.ordinal()])
                    {
                        case 1:
                            GL11.glVertexPointer(vertexformatelement.getElementCount(), l, i, bytebuffer);
                            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                            break;

                        case 2:
                            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + k);
                            GL11.glTexCoordPointer(vertexformatelement.getElementCount(), l, i, bytebuffer);
                            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                            break;

                        case 3:
                            GL11.glColorPointer(vertexformatelement.getElementCount(), l, i, bytebuffer);
                            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                            break;

                        case 4:
                            GL11.glNormalPointer(l, i, bytebuffer);
                            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                    }
                }
            }

            if (renderer.isMultiTexture())
            {
                renderer.drawMultiTexture();
            }
            else if (Config.isShaders())
            {
                SVertexBuilder.drawArrays(renderer.getDrawMode(), 0, renderer.getVertexCount(), renderer);
            }
            else
            {
                GL11.glDrawArrays(renderer.getDrawMode(), 0, renderer.getVertexCount());
            }

            int i1 = 0;

            for (int k1 = list.size(); i1 < k1; ++i1)
            {
                VertexFormatElement vertexformatelement1 = list.get(i1);
                VertexFormatElement.EnumUsage vertexformatelement$enumusage1 = vertexformatelement1.getUsage();

                if (flag1)
                {
                    Reflector.callVoid(vertexformatelement$enumusage1, Reflector.ForgeVertexFormatElementEnumUseage_postDraw, new Object[] {vertexformat, Integer.valueOf(i1), Integer.valueOf(i), bytebuffer});
                }
                else
                {
                    int j1 = vertexformatelement1.getIndex();

                    switch (WorldVertexBufferUploader.WorldVertexBufferUploader$1.field_178958_a[vertexformatelement$enumusage1.ordinal()])
                    {
                        case 1:
                            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
                            break;

                        case 2:
                            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + j1);
                            GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                            break;

                        case 3:
                            GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                            GlStateManager.resetColor();
                            break;

                        case 4:
                            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
                    }
                }
            }
        }

        renderer.reset();
    }

    static final class WorldVertexBufferUploader$1
    {
        static final int[] field_178958_a = new int[VertexFormatElement.EnumUsage.values().length];
        private static final String __OBFID = "CL_00002566";

        static
        {
            try
            {
                field_178958_a[VertexFormatElement.EnumUsage.POSITION.ordinal()] = 1;
            }
            catch (NoSuchFieldError var4)
            {
                ;
            }

            try
            {
                field_178958_a[VertexFormatElement.EnumUsage.UV.ordinal()] = 2;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try
            {
                field_178958_a[VertexFormatElement.EnumUsage.COLOR.ordinal()] = 3;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try
            {
                field_178958_a[VertexFormatElement.EnumUsage.NORMAL.ordinal()] = 4;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }
}
