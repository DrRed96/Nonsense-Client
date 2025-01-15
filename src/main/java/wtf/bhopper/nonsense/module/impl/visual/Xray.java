package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;

@ModuleInfo(name = "Xray",
        description = "Allows you to see ores thought blocks",
        category = ModuleCategory.VISUAL)
public class Xray extends Module {

    private final GroupProperty ores = new GroupProperty("Ores", "Ores to be rendered", this);
    private final BooleanProperty diamond = new BooleanProperty("Diamond", "Render diamond ores", true);

    private final NumberProperty opacity = new NumberProperty("Opacity", "Opacity of other blocks", 10, 0, 255, 1, NumberProperty.FORMAT_INT);

    private float lastGamma;

    public Xray() {
        super();
        this.ores.addProperties(this.diamond);
        this.addProperties(this.ores, this.opacity);
    }

    @Override
    public void onEnable() {
        this.lastGamma = mc.gameSettings.gammaSetting;
        mc.gameSettings.gammaSetting = 1000.0F;
        mc.renderGlobal.loadRenderers();
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = this.lastGamma;
        mc.renderGlobal.loadRenderers();
    }

    @EventLink
    public final Listener<EventTick> onTick = _ -> {
        if (PlayerUtil.canUpdate()) {
            mc.gameSettings.gammaSetting = 1000.0F;
        }
    };

    public boolean isBlockValid(Block block) {
        if (block == Blocks.diamond_ore) {
            return this.diamond.get();
        }

        return false;
    }

    public int getOpacity() {
        return this.opacity.getInt();
    }

}
