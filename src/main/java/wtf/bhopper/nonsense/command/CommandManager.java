package wtf.bhopper.nonsense.command;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.impl.*;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventChat;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private static final String PREFIX = ".";

    private final List<Command> commands = new ArrayList<>();

    public CommandManager() {

        this.commands.add(new Bind());
        this.commands.add(new ConfigCmd());
        this.commands.add(new Debug());
        this.commands.add(new Help());
        this.commands.add(new Say());
        this.commands.add(new Toggle());

        Nonsense.getEventBus().subscribe(this);
    }

    public List<Command> getCommands() {
        return this.commands;
    }

    @EventLink
    private final Listener<EventChat> onChat = event -> {
        if (event.message.startsWith(PREFIX)) {
            event.cancel();

            String[] args = event.message.split("\\s+");
            String commandName = args[0].substring(PREFIX.length());

            for (Command command : this.commands) {
                for (String alias : command.alias) {
                    if (alias.equalsIgnoreCase(commandName)) {
                        try {
                            command.execute(args, event.message);
                        } catch (CommandExecutionException exception) {
                            ChatUtil.error("%s", exception.getMessage());
                        } catch (Throwable throwable) {
                            ChatUtil.error("Error: %s", throwable.getMessage());
                        }
                        return;
                    }
                }
            }

            ChatUtil.error("'%s' is not a command.", commandName.toLowerCase());
        }
    };

}
