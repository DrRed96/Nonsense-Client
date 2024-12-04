package wtf.bhopper.nonsense.command;

import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;

public abstract class Command implements MinecraftInstance {

    public final String name = this.getClass().getAnnotation(CommandInfo.class).name();
    public final String description = this.getClass().getAnnotation(CommandInfo.class).description();
    public final String syntax = this.getClass().getAnnotation(CommandInfo.class).syntax();
    public final String[] alias =  GeneralUtil.concat(new String[]{name.toLowerCase()}, this.getClass().getAnnotation(CommandInfo.class).alias());

    public abstract void execute(String[] args, String rawCommand) throws Exception;

}
