package wtf.bhopper.nonsense.command.impl;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;

@CommandInfo(name = "Hide",
        description = "Hides or shows a module in the array list",
        syntax = ".hide <module>/all/none")
public class Hide extends Command {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {
        if (args.length < 2) {
            ChatUtil.error("Missing arguments: .show <module>");
            return;
        }

        if (args[1].equalsIgnoreCase("all")) {
            Nonsense.getModuleManager().getModules().forEach(module -> module.setHidden(true));
            ChatUtil.info("All modules are now hidden.");
            return;
        }

        if (args[1].equalsIgnoreCase("none")) {
            Nonsense.getModuleManager().getModules().forEach(module -> module.setHidden(false));
            ChatUtil.info("All modules are now displayed.");
            return;
        }

        Module module = Nonsense.getModuleManager().get(args[1]);
        if (module == null) {
            ChatUtil.error("'%s' is not a valid module", args[1].toLowerCase());
            return;
        }

        module.setHidden(!module.isHidden());
        ChatUtil.info("%s is now %s.", module.name, module.isHidden() ? "hidden" : "displayed");
    }
}
