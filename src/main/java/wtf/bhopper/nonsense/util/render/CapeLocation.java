package wtf.bhopper.nonsense.util.render;

import com.google.common.base.Objects;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class CapeLocation {

    public final ResourceLocation cape;
    public final ResourceLocation overlay;
    public final Color overlayColor;

    public CapeLocation(ResourceLocation cape, ResourceLocation overlay, Color overlayColor) {
        this.cape = cape;
        this.overlay = overlay;
        this.overlayColor = overlayColor;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("cape", this.cape)
                .add("overlay", this.overlay)
                .add("overlayColor", this.overlayColor)
                .toString();
    }

}
