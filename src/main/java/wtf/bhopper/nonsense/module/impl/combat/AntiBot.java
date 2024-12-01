package wtf.bhopper.nonsense.module.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import wtf.bhopper.nonsense.event.EventPriorities;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.minecraft.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.ServerUtil;

import java.util.HashSet;
import java.util.Set;

@ModuleInfo(name = "Anti Bot", description = "Prevents bot targetting", category = ModuleCategory.COMBAT)
public class AntiBot extends Module {

    private static final String VALID_USERNAME_REGEX = "^[a-zA-Z0-9_]{1,16}+$";

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Method for bot detection", Mode.TAB);

    private final Set<EntityPlayer> bots = new HashSet<>();

    public AntiBot() {
        this.addProperties(this.mode);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @EventLink(EventPriorities.VERY_LOW)
    public final Listener<EventTick> onTick = event -> {
        if (!PlayerUtil.canUpdate()) {
            this.bots.clear();
            return;
        }

        switch (this.mode.get()) {
            case TAB -> {
                this.bots.clear();
                this.bots.addAll(mc.theWorld.getEntities(EntityPlayer.class, input -> !ServerUtil.isInTab(input)));
            }
            case HYPIXEL -> {
                this.bots.clear();
                for (EntityPlayer player : mc.theWorld.getEntities(EntityPlayer.class, input -> true)) {
                    if (!ServerUtil.isInTab(player) || this.nameStartsWith(player, "[NPC] ") || !player.getName().matches(VALID_USERNAME_REGEX)) {
                        this.bots.add(player);
                    }
                }
            }
        }


    };

    public boolean isBot(Entity entity) {
        if (!this.isToggled()) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            return this.bots.contains(entity);
        }
        return false;
    }

    public boolean isBot(EntityPlayer entityPlayer)  {
        if (!this.isToggled()) {
            return false;
        }
        return this.bots.contains(entityPlayer);
    }

    private boolean nameStartsWith(EntityPlayer player, String prefix) {
        return EnumChatFormatting.getTextWithoutFormattingCodes(player.getDisplayName().getUnformattedText()).startsWith(prefix);
    }

    private enum Mode {
        TAB,
        HYPIXEL
    }

}
