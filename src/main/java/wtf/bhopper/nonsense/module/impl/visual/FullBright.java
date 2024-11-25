package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.minecraft.PlayerUtil;

@ModuleInfo(name = "Full Bright", description = "Gives you night vision.", category = ModuleCategory.VISUAL)
public class FullBright extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Method for brightness", Mode.GAMMA);

    private float lastGamma;
    private boolean addedEffect;
    private boolean hadEffect;

    public FullBright() {
        this.addProperties(this.mode);
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
        switch (this.mode.get()) {
            case POTION -> {
                if (this.addedEffect && !this.hadEffect) {
                    mc.thePlayer.removePotionEffect(Potion.nightVision.id);
                }
            }
            case GAMMA -> mc.gameSettings.gammaSetting = this.lastGamma;
        }
    }

    @EventLink
    public final Listener<EventTick> onTick = event -> {
        if (!PlayerUtil.canUpdate()) {
            return;
        }

        switch (this.mode.get()) {
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

    };

    private enum Mode {
        GAMMA,
        POTION
    }

}
