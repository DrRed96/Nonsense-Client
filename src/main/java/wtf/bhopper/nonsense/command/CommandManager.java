package wtf.bhopper.nonsense.command;

import org.reflections.Reflections;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.impl.Help;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventChat;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CommandManager {

    public static final String PREFIX = ".";

    private final List<Command> commands = new ArrayList<>();

    public CommandManager() {
        new Reflections(Help.class.getPackage().getName())
                .getSubTypesOf(Command.class)
                .stream()
                .sorted(Comparator.comparing(clazz -> clazz.getAnnotation(CommandInfo.class).name()))
                .forEach(command -> {
                    try {
                        this.commands.add(command.getConstructor().newInstance());
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }
                });
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
                        } catch (Throwable throwable) {
                            ChatUtil.error("Error: %s", throwable.getMessage());
                            Nonsense.LOGGER.error("Command Error", throwable);
                        }
                        return;
                    }
                }
            }

            ChatUtil.error("'%s' is not a command.", commandName.toLowerCase());
        }
    };

}
