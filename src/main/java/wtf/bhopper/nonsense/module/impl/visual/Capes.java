package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.util.ResourceLocation;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.annotations.Description;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.render.CapeLocation;
import wtf.bhopper.nonsense.util.render.ColorUtil;

import java.awt.*;
import java.util.function.Supplier;

@ModuleInfo(name = "Capes",
        description = "Client capes.",
        category = ModuleCategory.VISUAL,
        toggled = true)
public class Capes extends Module {

    public final EnumProperty<Cape> cape = new EnumProperty<>("Cape", "There's too many of them...", Cape.NONSENSE);
    public final BooleanProperty glint = new BooleanProperty("Enchanted", "Renders an enchantment glint over the cape", false);

    // Frame counter for animated capes
    private int frameCounter = 0;

    public Capes() {
        super();
        this.addProperties(this.cape, this.glint);
        this.cape.addValueChangeListener((_, _) -> this.frameCounter = 0);
    }

    @Override
    public void onEnable() {
        this.frameCounter = 0;
    }

    @EventLink
    public final Listener<EventTick> onTick = _ -> {
        if (!PlayerUtil.canUpdate()) {
            this.frameCounter = 0;
            return;
        }
        ICape cape = this.cape.get().cape;
        if (cape instanceof AnimatedCape animatedCape) {
            if (mc.thePlayer.ticksExisted % animatedCape.tickDelay == 0) {
                frameCounter++;
            }
        }
    };

    public enum Cape {
        ASTOLFO,
        CROSSSINE,
        DAYS,
        DIABLO,
        DORTWARE,
        EXHIBITION(new OverlayCape("exhibition", () -> new Color(ColorUtil.exhiRainbow(System.currentTimeMillis(), 0)))),
        EXHIBITION_2(new OverlayCape("exhibition2", () -> new Color(ColorUtil.exhiRainbow(System.currentTimeMillis(), 0)))),
        FUTURE,
        GATO,
        LIGHTNING(new AnimatedCape("lightning", 11, 3)),
        MILLION,
        MINECON_2011,
        MINECON_2012,
        MINECON_2013,
        MINECON_2015,
        MINECON_2016,
        MONSOON,
        MOON,
        NONSENSE,
        @DisplayName("Novoline.wtf") NOVOLINE_WTF,
        @DisplayName("OptiFine") OPTIFINE,
        REACTOR,
        RISE_5(new AnimatedCape("rise_5", 14, 3)),
        RISE_6,
        SLACK,
        SKIDWARE,
        @Description("Ok who tf would use this though?") TEMPLATE,
        TENACITY,
        XYLANS;

        public final ICape cape;

        Cape() {
            this.cape = new StaticCape(this.name().toLowerCase());
        }

        Cape(ICape cape) {
            this.cape = cape;
        }

        public CapeLocation getResource() {
            return this.cape.getResource();
        }

        public enum Type {
            STATIC,
            OVERLAY,
            ANIMATED
        }

    }

    public static class StaticCape implements ICape {

        private final ResourceLocation location;

        public StaticCape(String name) {
            this.location = new ResourceLocation(String.format("nonsense/capes/%s.png", name));
        }

        @Override
        public CapeLocation getResource() {
            return new CapeLocation(location, null, Color.WHITE);
        }
    }

    public static class OverlayCape implements ICape {

        private final ResourceLocation location;
        private final ResourceLocation overlay;
        private final Supplier<Color> color;

        public OverlayCape(String name, Supplier<Color> color) {
            this.location = new ResourceLocation(String.format("nonsense/capes/%s.png", name));
            this.overlay = new ResourceLocation(String.format("nonsense/capes/%s_overlay.png", name));
            this.color = color;
        }

        @Override
        public CapeLocation getResource() {
            return new CapeLocation(location, overlay, color.get());
        }
    }

    public static class AnimatedCape implements ICape {

        private final ResourceLocation[] locations;
        private final int frames;
        private final int tickDelay;

        public AnimatedCape(String name, int frames, int tickDelay) {
            this.locations = new ResourceLocation[frames];
            this.frames = frames;
            this.tickDelay = tickDelay;
            for (int i = 0; i < frames; i++) {
                locations[i] = new ResourceLocation(String.format("nonsense/capes/%s/%d.jpg", name, i + 1));
            }
        }


        @Override
        public CapeLocation getResource() {
            int frameCounter = Nonsense.module(Capes.class).frameCounter;
            return new CapeLocation(locations[frameCounter % frames], null, Color.WHITE);
        }
    }

    public interface ICape {
        CapeLocation getResource();
    }

}
