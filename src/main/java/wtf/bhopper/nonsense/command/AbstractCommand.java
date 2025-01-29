package wtf.bhopper.nonsense.command;

import wtf.bhopper.nonsense.util.minecraft.IMinecraft;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;

public abstract class AbstractCommand implements IMinecraft {

    public final String name;
    public final String description;
    public final String syntax;
    public final String[] alias;

    public AbstractCommand() {
        this.name = this.getClass().getAnnotation(CommandInfo.class).name();
        this.description = this.getClass().getAnnotation(CommandInfo.class).description();
        this.syntax = this.getClass().getAnnotation(CommandInfo.class).syntax();
        this.alias =  GeneralUtil.concat(new String[]{name.toLowerCase()}, this.getClass().getAnnotation(CommandInfo.class).alias());
    }

    public abstract void execute(String[] args, String rawCommand) throws Exception;

}
