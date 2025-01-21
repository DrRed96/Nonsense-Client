package net.minecraft.realms;

import net.minecraft.client.renderer.Tessellator;

public class Tezzelator
{
    public static Tessellator t = Tessellator.getInstance();
    public static final Tezzelator instance = new Tezzelator();

    public void end()
    {
        t.draw();
    }

    public Tezzelator vertex(double x, double y, double z)
    {
        t.getWorldRenderer().pos(x, y, z);
        return this;
    }

    public void color(float red, float green, float blue, float alpha)
    {
        t.getWorldRenderer().color(red, green, blue, alpha);
    }

    public void tex2(short u, short v)
    {
        t.getWorldRenderer().lightmap(u, v);
    }

    public void normal(float nx, float ny, float nz)
    {
        t.getWorldRenderer().normal(nx, ny, nz);
    }

    public void begin(int p_begin_1_, RealmsVertexFormat p_begin_2_)
    {
        t.getWorldRenderer().begin(p_begin_1_, p_begin_2_.getVertexFormat());
    }

    public void endVertex()
    {
        t.getWorldRenderer().endVertex();
    }

    public void offset(double x, double y, double z)
    {
        t.getWorldRenderer().setTranslation(x, y, z);
    }

    public RealmsBufferBuilder color(int red, int green, int blue, int alpha)
    {
        return new RealmsBufferBuilder(t.getWorldRenderer().color(red, green, blue, alpha));
    }

    public Tezzelator tex(double u, double v)
    {
        t.getWorldRenderer().tex(u, v);
        return this;
    }
}
