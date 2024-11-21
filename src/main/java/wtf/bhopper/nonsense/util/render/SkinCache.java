package wtf.bhopper.nonsense.util.render;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import wtf.bhopper.nonsense.alt.Alt;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class SkinCache {

    public static final ResourceLocation STEVE_LOCATION = new ResourceLocation("textures/entity/steve.png");

    private static final Map<UUID, ResourceLocation> cachedLocations = new LinkedHashMap<>();

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static ResourceLocation getSkin(Alt alt) {
        return getSkin(alt.getProfile());
    }

    public static ResourceLocation getSkin(GameProfile profile) {

        if (!cachedLocations.containsKey(profile.getId())) {
            new Thread(() -> {
                try {
                    SkinCache.mc.getSessionService().fillProfileProperties(profile, true);
                    SkinCache.mc.getSkinManager().loadProfileTextures(profile, (type, location, profileTexture) -> {
                        if (type == MinecraftProfileTexture.Type.SKIN) {
                            cachedLocations.put(profile.getId(), location);
                        }
                    }, false);
                } catch (Exception ignored) {}
            }).start();
            return STEVE_LOCATION;
        }

        return cachedLocations.get(profile.getId());

    }

    static {
        cachedLocations.put(null, STEVE_LOCATION);
    }

}
