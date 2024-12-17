package wtf.bhopper.nonsense.command.impl;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.property.Property;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

@CommandInfo(name = "Set",
        description = "Sets the value of a property.",
        syntax = ".set <module> <setting> <value>")
public class SetCmd extends Command {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {
        if (args.length < 4) {
            ChatUtil.error("Invalid arguments: %s", this.syntax);
            return;
        }

        Module module = Nonsense.getModuleManager().get(args[1]);
        if (module == null) {
            ChatUtil.error("'%s' is not a module", args[1]);
            return;
        }

        Property<?> property = module.getProperty(args[2]);
        if (property == null) {
            ChatUtil.error("'%s' is not a property", args[2]);
            return;
        }

        this.parse(property, args, 3);
    }

    private void parse(Property<?> property, String[] args, int current) {
        if (property instanceof GroupProperty groupProperty) {
            if (args.length < current + 2) {
                ChatUtil.error("Invalid arguments: %s", this.syntax);
                return;
            }
            Property<?> property2 = groupProperty.getProperty(args[current]);
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
