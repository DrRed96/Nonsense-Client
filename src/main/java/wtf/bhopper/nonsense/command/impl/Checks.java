package wtf.bhopper.nonsense.command.impl;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.anticheat.check.Check;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.CommandInfo;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;

@CommandInfo(name = "Checks",
        description = "Displays the list of anticheat checks",
        syntax = ".checks")
public class Checks extends Command {
    @Override
    public void execute(String[] args, String rawCommand) throws Exception {
        ChatUtil.title("Checks");
        for (Check check : Nonsense.getAntiCheat().getChecks()) {
            ChatUtil.Builder.of("%s\247c%s\2477: %s", ChatUtil.CHAT_PREFIX_SHORT, check.name, check.description)
                    .setHoverEvent(GeneralUtil.paragraph(
                            "\247c\247l" + check.name,
                            "\2477" + check.description + " \2478(VL: " + check.maxVl + ")"
                    ))
                    .send();
        }
    }
}
