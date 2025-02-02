package wtf.bhopper.nonsense.module.impl.other;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;


@ModuleInfo(name = "Discord RPC",
        description = "Discord Rich Presence.",
        category = ModuleCategory.OTHER,
        hidden = true)
public class DiscordRPCMod extends AbstractModule {

    private static final String APPLICATION_ID = "1294195120781922305";

    public DiscordRPCMod() {
        super();
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                .setReadyEventHandler(user -> Nonsense.LOGGER.info("[Discord RPC] Logged in as {}:{}", user.username, user.userId))
                .build();

        DiscordRPC.discordInitialize(APPLICATION_ID, handlers, true);

        Runtime.getRuntime().addShutdownHook(new Thread(DiscordRPC::discordShutdown));
    }

    @Override
    public void onDisable() {
        DiscordRPC.discordClearPresence();
    }

    @EventLink
    public final Listener<EventTick> onTick = event -> {
        DiscordRichPresence presence = new DiscordRichPresence
                .Builder(String.format("%d/%d modules enabled", Nonsense.getModuleManager().amountEnabled(), Nonsense.getModuleManager().size()))
                .setStartTimestamps(Nonsense.getStartTime())
                .setBigImage("icon", "https://bhopper.wtf")
                .build();

        DiscordRPC.discordUpdatePresence(presence);
    };

}
