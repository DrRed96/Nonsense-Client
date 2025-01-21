package net.minecraft.client.renderer;

import net.minecraft.client.renderer.vertex.VertexBuffer;

public class VertexBufferUploader extends WorldVertexBufferUploader
{
    private VertexBuffer vertexBuffer = null;

    public void upload(WorldRenderer renderer)
    {
        renderer.reset();
        this.vertexBuffer.func_181722_a(renderer.getByteBuffer());
    }

    public void setVertexBuffer(VertexBuffer vertexBufferIn)
    {
        this.vertexBuffer = vertexBufferIn;
    }
}
