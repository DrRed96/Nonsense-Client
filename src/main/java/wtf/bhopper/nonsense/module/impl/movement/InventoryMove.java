package wtf.bhopper.nonsense.module.impl.movement;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventPostMotion;
import wtf.bhopper.nonsense.gui.click.novoline.NovoButton;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;

@ModuleInfo(name = "Inventory Move", description = "Allows you to move whilst in your inventory or other GUI's", category = ModuleCategory.MOVEMENT)
public class InventoryMove extends Module {

    private final BooleanProperty clientSide = new BooleanProperty("Client Side Only", "Only move in client sided GUI's", false);

    private final KeyBinding[] keyBindings = new KeyBinding[] {
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindJump
    };

    public InventoryMove() {
        this.addProperties(this.clientSide);
    }

    @EventLink
    public final Listener<EventPostMotion> onPost = event -> {
        if (this.canInvMove()) {
            for (KeyBinding keyBinding : keyBindings) {
                keyBinding.setPressed(GameSettings.isKeyDown(keyBinding));
            }
        }
    };

    private boolean canInvMove() {

        if (mc.currentScreen == null || mc.currentScreen instanceof GuiChat) {
            return false;
        }

        if (this.clientSide.get()) {
            return mc.currentScreen.isClientSide();
        }

        return true;
    }

}
