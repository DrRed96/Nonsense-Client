package wtf.bhopper.nonsense.util.misc;

import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ResourceUtil {

    public static String getPathFromLocation(ResourceLocation location) {
        return "assets/" + location.getResourceDomain() + "/" + location.getResourcePath();
    }

    public static ByteBuffer loadResource(ResourceLocation resource, int bufferSize) throws IOException {
        return IOUtil.ioResourceToByteBuffer(getPathFromLocation(resource), bufferSize);
    }

    public static ByteBuffer loadResource(String resource, int bufferSize) throws IOException {
        return IOUtil.ioResourceToByteBuffer(resource, bufferSize);
    }

    public static ByteBuffer loadResource(ResourceLocation resource) throws IOException {
        return IOUtil.ioResourceToByteBuffer(getPathFromLocation(resource));
    }

    public static ByteBuffer loadResource(String resource) throws IOException {
        return IOUtil.ioResourceToByteBuffer(resource);
    }

}
