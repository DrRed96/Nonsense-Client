package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.util.IChatComponent;
import wtf.bhopper.nonsense.event.EventPriorities;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.packet.EventReceivePacket;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;

@ModuleInfo(name = "Better Chat",
        description = "Improves your chat",
        category = ModuleCategory.VISUAL)
public class BetterChat extends AbstractModule {

    private final BooleanProperty chatStacker = new BooleanProperty("Chat Stacker", "Stacks duplicate chat messages.", true);
    private final BooleanProperty noClose = new BooleanProperty("No Close", "Prevents the server from closing your chat.", true);
    private final BooleanProperty noBackground = new BooleanProperty("No Background", "Prevents the chat background from rendering.", false);
    private String lastMessage = "";
    private int amount = 1;
    private int line = 0;

    public BetterChat() {
        super();
        this.addProperties(this.chatStacker, this.noClose, this.noBackground);
    }

    @EventLink(EventPriorities.LOW)
    public final Listener<EventReceivePacket> onReceivePacket = event -> {
        if (event.packet instanceof S02PacketChat packet) {
            if (packet.getType() == (byte) 0 && this.chatStacker.get() && !event.isCancelled()) {
                event.cancel();

                IChatComponent component = packet.getChatComponent();
                String rawMessage = component.getUnformattedText();
                GuiNewChat chat = mc.ingameGUI.getChatGUI();
                if (this.lastMessage.equals(rawMessage)) {
                    chat.deleteChatLine(this.line);
                    this.amount++;
                    packet.getChatComponent().appendText(" \2477(x" + this.amount + ")");
                } else {
                    this.amount = 1;
                }
                this.line++;
                this.lastMessage = rawMessage;
                chat.printChatMessageWithOptionalDeletion(component, line);

                if (this.line > 256) {
                    this.line = 0;
                }
            }

        }

        if (this.noClose.get() && event.packet instanceof S2EPacketCloseWindow && mc.currentScreen instanceof GuiChat) {
            event.cancel();
        }
    };;

    public boolean noBackground() {
        return this.isToggled() && this.noBackground.get();
    }

}
