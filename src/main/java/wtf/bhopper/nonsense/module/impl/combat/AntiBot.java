package wtf.bhopper.nonsense.module.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.EventPriorities;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.world.ServerUtil;

import java.util.HashSet;
import java.util.Set;

@ModuleInfo(name = "Anti Bot",
        description = "Prevents bot targeting",
        category = ModuleCategory.COMBAT)
public class AntiBot extends Module {

    private static final String VALID_USERNAME_REGEX = "^[a-zA-Z0-9_]{1,16}+$";

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Method for bot detection", Mode.TAB);

    private final Set<EntityPlayer> bots = new HashSet<>();

    public AntiBot() {
        super();
        this.addProperties(this.mode);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @EventLink(EventPriorities.VERY_HIGH)
    public final Listener<EventTick> onTick = _ -> {
        if (!PlayerUtil.canUpdate()) {
            this.bots.clear();
            return;
        }

        switch (this.mode.get()) {
            case TAB -> {
                this.bots.clear();
                this.bots.addAll(mc.theWorld.getEntities(EntityPlayer.class, entity -> !ServerUtil.isInTab(entity)));
            }

            case NPC -> {
                this.bots.clear();
                this.bots.addAll(mc.theWorld.getEntities(EntityPlayer.class, entity -> !entity.hasMoved));
            }

            case HYPIXEL -> {
                this.bots.clear();
                for (EntityPlayer player : mc.theWorld.getEntities(EntityPlayer.class, _ -> true)) {
                    if (!ServerUtil.isInTab(player) ||
                            this.nameStartsWith(player, "[NPC] ") ||
                            !player.getName().matches(VALID_USERNAME_REGEX) ||
                            !player.hasMoved) {
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
        @DisplayName("NPC") NPC,
        HYPIXEL
    }

}
