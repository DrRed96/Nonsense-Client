package net.minecraft.client.renderer;

import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.EnumWorldBlockLayer;
import optifine.Config;

import org.lwjgl.opengl.GL11;

public class RenderList extends ChunkRenderContainer {

    public void renderChunkLayer(EnumWorldBlockLayer layer) {
        if (this.initialized) {
            if (this.renderChunks.isEmpty()) {
                return;
            }

            for (RenderChunk renderchunk : this.renderChunks) {
                ListedRenderChunk listedrenderchunk = (ListedRenderChunk) renderchunk;
                GlStateManager.pushMatrix();
                this.preRenderChunk(renderchunk);
                GL11.glCallList(listedrenderchunk.getDisplayList(layer, listedrenderchunk.getCompiledChunk()));
                GlStateManager.popMatrix();
            }

            if (Config.isMultiTexture()) {
                GlStateManager.bindCurrentTexture();
            }

            GlStateManager.resetColor();
            this.renderChunks.clear();
        }
    }
}
