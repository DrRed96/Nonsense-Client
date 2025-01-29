package wtf.bhopper.nonsense.command.impl;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.AbstractCommand;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

@CommandInfo(name = "Toggle",
        description = "Toggles a module.",
        syntax = ".toggle <module>",
        alias = "t")
public class Toggle extends AbstractCommand {

    @Override
    public void execute(String[] args, String rawCommand) throws Exception {
        if (args.length < 2) {
            ChatUtil.error("Missing arguments: %s", this.syntax);
            return;
        }

        AbstractModule module = Nonsense.getModuleManager().get(args[1]);

        if (module == null) {
            ChatUtil.error("'%s' is not a module.", args[1].toLowerCase());
            return;
        }

        module.toggle();
        ChatUtil.info("%s was %s", module.displayName, module.isToggled() ? "\247aEnabled" : "\247cDisabled");
    }

}
