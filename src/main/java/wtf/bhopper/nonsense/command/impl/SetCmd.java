package wtf.bhopper.nonsense.command.impl;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.AbstractCommand;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.property.AbstractProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

@CommandInfo(name = "Set",
        description = "Sets the value of a property.",
        syntax = ".set <module> <setting> <value>")
public class SetCmd extends AbstractCommand {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {
        if (args.length < 4) {
            ChatUtil.error("Invalid arguments: %s", this.syntax);
            return;
        }

        AbstractModule module = Nonsense.getModuleManager().get(args[1]);
        if (module == null) {
            ChatUtil.error("'%s' is not a module", args[1]);
            return;
        }

        AbstractProperty<?> property = module.getProperty(args[2]);
        if (property == null) {
            ChatUtil.error("'%s' is not a property", args[2]);
            return;
        }

        this.parse(property, args, 3);
    }

    private void parse(AbstractProperty<?> property, String[] args, int current) {
        if (property instanceof GroupProperty groupProperty) {
            if (args.length < current + 2) {
                ChatUtil.error("Invalid arguments: %s", this.syntax);
                return;
            }
            AbstractProperty<?> property2 = groupProperty.getProperty(args[current]);
            if (property2 == null) {
                ChatUtil.error("'%s' is not a property", args[current]);
                return;
            }

            this.parse(property2, args, current + 1);

        } else {
            try {
                property.parseString(args[current]);
                ChatUtil.info("'%s' was set to: %s", property.name, property.getDisplayValue());
            } catch (IllegalArgumentException exception) {
                ChatUtil.error("'%s' cannot be set to: %s", property.name, args[current]);
            } catch (UnsupportedOperationException exception) {
                ChatUtil.error("'%s' cannot be set", property.name);
            }
        }
    }
}
