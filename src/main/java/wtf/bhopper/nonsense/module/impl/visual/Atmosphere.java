package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.packet.EventReceivePacket;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.*;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.render.ColorUtil;

@ModuleInfo(name = "Atmosphere", description = "Modifies the atmosphere", category = ModuleCategory.VISUAL)
public class Atmosphere extends Module {

    private final EnumProperty<FullBright> fullBright = new EnumProperty<>("Full Bright", "Method for brightness", FullBright.GAMMA);
    
    private final GroupProperty timeGroup = new GroupProperty("Time", "Change the world time", this);
    private final BooleanProperty timeEnable = new BooleanProperty("Enable", "Enables time changer", false);
    private final EnumProperty<TimeMode> timeMode = new EnumProperty<>("Mode", "How to change the time", TimeMode.CUSTOM);
    private final NumberProperty timeChange = new NumberProperty("Time", "What time to change to", () -> this.timeMode.is(TimeMode.CUSTOM), 18000, 0, 24000, 300);

    private final GroupProperty weatherGroup = new GroupProperty("Weather", "Changes the weather", this);
    private final BooleanProperty weatherEnabled = new BooleanProperty("Enabled", "Enables weather changer", true);
    private final EnumProperty<Weather> weather = new EnumProperty<>("Weather", "What weather to use", Weather.CLEAR);

    private final GroupProperty worldColorGroup = new GroupProperty("World Color", "Modifies the world color", this);
    private final BooleanProperty worldColorEnable = new BooleanProperty("Enable", "Enables world color", false);
    public final ColorProperty worldColor = new ColorProperty("Color", "World color", ColorUtil.WHITE);

    private float lastGamma;
    private boolean addedEffect;
    private boolean hadEffect;

    public Atmosphere() {
        this.timeGroup.addProperties(this.timeEnable, this.timeMode, this.timeChange);
        this.weatherGroup.addProperties(this.weatherEnabled, this.weather);
        this.worldColorGroup.addProperties(this.worldColorEnable, this.worldColor);
        this.addProperties(this.fullBright, this.timeGroup, this.weatherGroup, this.worldColorGroup);
    }

    @Override
    public void onEnable() {
        this.lastGamma = mc.gameSettings.gammaSetting;
        try {
            this.hadEffect = mc.thePlayer.isPotionActive(Potion.nightVision);
        } catch (NullPointerException ignored) {
            this.hadEffect = false;
        }
    }

    @Override
    public void onDisable() {
        switch (this.fullBright.get()) {
            case POTION -> {
                if (this.addedEffect && !this.hadEffect) {
                    mc.thePlayer.removePotionEffect(Potion.nightVision.id);
                }
            }
            case GAMMA -> mc.gameSettings.gammaSetting = this.lastGamma;
        }
    }

    @EventLink
    public final Listener<EventTick> onTick = _ -> {
        if (!PlayerUtil.canUpdate()) {
            return;
        }

        switch (this.fullBright.get()) {
            case GAMMA -> {
                if (!this.hadEffect && mc.thePlayer.isPotionActive(Potion.nightVision)) {
                    mc.thePlayer.removePotionEffect(Potion.nightVision.id);
                }
                mc.gameSettings.gammaSetting = 1000.0F;
            }
            case POTION -> {
                if (!this.hadEffect) {
                    mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 5210, 68));
                    this.addedEffect = true;
                }
            }
        }

        if (this.timeEnable.get()) {
            switch (this.timeMode.get()) {
                case CUSTOM -> mc.theWorld.setWorldTime(this.timeChange.getInt());
                case CYCLE -> mc.theWorld.setWorldTime(System.currentTimeMillis() % 24000L);
            }
        }

        if (this.weatherEnabled.get() && mc.thePlayer.ticksExisted % 20 == 0) {

            switch (this.weather.get()) {
                case CLEAR -> {
                    mc.theWorld.setRainStrength(0);
                    mc.theWorld.getWorldInfo().setCleanWeatherTime(Integer.MAX_VALUE);
                    mc.theWorld.getWorldInfo().setRainTime(0);
                    mc.theWorld.getWorldInfo().setThunderTime(0);
                    mc.theWorld.getWorldInfo().setRaining(false);
                    mc.theWorld.getWorldInfo().setThundering(false);
                }

                case RAIN -> {
                    mc.theWorld.setRainStrength(1);
                    mc.theWorld.getWorldInfo().setCleanWeatherTime(0);
                    mc.theWorld.getWorldInfo().setRainTime(Integer.MAX_VALUE);
                    mc.theWorld.getWorldInfo().setThunderTime(Integer.MAX_VALUE);
                    mc.theWorld.getWorldInfo().setRaining(true);
                    mc.theWorld.getWorldInfo().setThundering(false);
                }

                case THUNDER -> {
                    mc.theWorld.setRainStrength(1);
                    mc.theWorld.getWorldInfo().setCleanWeatherTime(0);
                    mc.theWorld.getWorldInfo().setRainTime(Integer.MAX_VALUE);
                    mc.theWorld.getWorldInfo().setThunderTime(Integer.MAX_VALUE);
                    mc.theWorld.getWorldInfo().setRaining(true);
                    mc.theWorld.getWorldInfo().setThundering(true);
                }
            }
        }

    };

    @EventLink
    public final Listener<EventReceivePacket> onReceivePacket = event -> {
        if (this.timeEnable.get() && event.packet instanceof S03PacketTimeUpdate) {
            event.cancel();
        }
    };

    public boolean worldColor() {
        return this.isToggled() && this.worldColorEnable.get();
    }

    private enum FullBright {
        GAMMA,
        POTION,
        NONE
    }
    
    private enum TimeMode {
        CUSTOM,
        CYCLE
    }

    private enum Weather {
        CLEAR,
        RAIN,
        THUNDER
    }

}
