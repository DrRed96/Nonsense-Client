package wtf.bhopper.nonsense.util.minecraft.player;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.network.Account;
import wtf.bhopper.nonsense.util.minecraft.IMinecraft;

public class ChatUtil implements IMinecraft {

    public static final String CHAT_PREFIX = "\2478\247l[\247c\247lNonsense\2478\247l] \247r\2477";
    public static final String CHAT_PREFIX_SHORT = "\247f> \2477";
    public static final String DEBUG_PREFIX = "\2478[\2473DEBUG\2478] \247r\247b";
    public static final String ANTICHEAT_PREFIX = "\247d\247lAnti Cheat \247r";
    public static final String IRC_PREFIX = "\2476\247lIRC \247r";

    public static void raw(IChatComponent message) {
        mc.ingameGUI.getChatGUI().printChatMessage(message);
    }

    public static void raw(String message) {
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(message));
    }

    public static void print(String message, Object... args) {
        Builder.of("%s%s", CHAT_PREFIX_SHORT, String.format(message, args))
                .setColor(EnumChatFormatting.GRAY)
                .send();
    }

    public static void info(String message, Object... args) {
        Builder.of("%s%s", CHAT_PREFIX, String.format(message, args))
                .setColor(EnumChatFormatting.GRAY)
                .send();
    }

    public static void error(String message, Object... args) {
        Builder.of("%s\247c%s", CHAT_PREFIX, String.format(message, args))
                .setColor(EnumChatFormatting.RED)
                .send();
    }

    public static void title(String title) {
        print("\247c\247l--- %s ---", title);
    }

    public static void debugTitle(String title) {
        print("\247b\247l--- %s ---", title);
    }

    public static void debugItem(String name, Object value) {
        Builder.of("%s\247b%s\2478: \2477%s", CHAT_PREFIX_SHORT, name, String.valueOf(value))
                .setHoverEvent("Click to copy: " + name)
                .setClickEvent(ClickEvent.Action.RUN_COMMAND, ".copy " + value)
                .send();
    }

    public static void debugItems(String name, Object... values) {
        StringBuilder text = new StringBuilder(String.format("%s\247b%s\2478:\2477", CHAT_PREFIX_SHORT, name));
        for (Object value : values) {
            text.append(" ").append(value);
        }
        Builder.of(text.toString())
                .setHoverEvent("Click to copy: " + name)
//                .setClickEvent(ClickEvent.Action.RUN_COMMAND, ".copy " + value)
                .send();
    }

    public static void debug(String message, Object... args) {

//        if (!Nonsense.debug()) {
//            return;
//        }

        Nonsense.LOGGER.debug(String.format(message, args));

        Builder.of("%s\247b%s", DEBUG_PREFIX, String.format(message, args))
                .setColor(EnumChatFormatting.AQUA)
                .send();
    }

    public static void debugList(String name, String... items) {
        ChatUtil.print("\247b%s\2478:", name);
        for (String item : items) {
            if (item == null) {
                continue;
            }
            ChatUtil.print("  \2477%s", item);
        }
    }

    public static void irc(String message, Account account) {
        Builder.of("%s%s\2477: %s", IRC_PREFIX, account.getDisplayName(), message)
                .setColor(EnumChatFormatting.GRAY)
                .send();
    }

    public static void whisper(String message, Account account, boolean sending) {
        Builder.of(sending ? "%s\247dYou whisper to %s\2477: %s" : "%s%s\247d whispers to you\2477: %s", IRC_PREFIX, account.getDisplayName(), message)
                .setColor(EnumChatFormatting.GRAY)
                .send();
    }

    public static void send(String message, Object... args) {
        mc.thePlayer.sendChatMessage(String.format(message, args));
    }

    public static void sendNoEvent(String message, Object... args) {
        PacketUtil.send(new C01PacketChatMessage(String.format(message, args)));
    }

    public static NBTTagCompound convertToNBT(IChatComponent component) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("text", component.getFormattedText());

        ChatStyle style = component.getChatStyle();

        EnumChatFormatting color = style.getColor();
        if (color != null) {
            nbt.setString("color", color.getFriendlyName());
        }

        if (style.getBold()) nbt.setBoolean("bold", true);
        if (style.getItalic()) nbt.setBoolean("italic", true);
        if (style.getStrikethrough()) nbt.setBoolean("strikethrough", true);
        if (style.getUnderlined()) nbt.setBoolean("underlined", true);
        if (style.getObfuscated()) nbt.setBoolean("obfuscated", true);

        ClickEvent clickEvent = style.getChatClickEvent();
        if (clickEvent != null) {
            NBTTagCompound click = new NBTTagCompound();
            click.setString("action", clickEvent.getAction().getCanonicalName());
            click.setString("value", clickEvent.getValue());
        }

        HoverEvent hoverEvent = style.getChatHoverEvent();
        if (hoverEvent != null) {
            NBTTagCompound hover = new NBTTagCompound();
            hover.setString("action", hoverEvent.getAction().getCanonicalName());
            hover.setTag("value", convertToNBT(hoverEvent.getValue()));
        }

        return nbt;
    }

    /**
     * Utility class to easily build IChatComponents (Text).
     * @author Semx11
     * <https://gist.github.com/Semx11/e3c1a8df4d8667a6c30a6d01505418c5>
     */
    public static class Builder {

        private final IChatComponent parent;

        private final String text;
        private final ChatStyle style;

        private Builder(String text) {
            this(text, null, Inheritance.SHALLOW);
        }

        private Builder(String text, IChatComponent parent, Inheritance inheritance) {
            this.parent = parent;
            this.text = text;

            switch (inheritance) {
                case DEEP -> this.style = parent != null ? parent.getChatStyle() : new ChatStyle();
                case NONE -> this.style = new ChatStyle().setColor(null).setBold(false).setItalic(false)
                        .setStrikethrough(false).setUnderlined(false).setObfuscated(false)
                        .setChatClickEvent(null).setChatHoverEvent(null).setInsertion(null);
                default -> this.style = new ChatStyle();
            }
        }

        public static Builder of(String text) {
            return new Builder(text);
        }

        public static Builder of(String text, Object... args) {
            return new Builder(String.format(text, args));
        }

        public Builder setColor(EnumChatFormatting color) {
            style.setColor(color);
            return this;
        }

        public Builder setBold(boolean bold) {
            style.setBold(bold);
            return this;
        }

        public Builder setItalic(boolean italic) {
            style.setItalic(italic);
            return this;
        }

        public Builder setStrikethrough(boolean strikethrough) {
            style.setStrikethrough(strikethrough);
            return this;
        }

        public Builder setUnderlined(boolean underlined) {
            style.setUnderlined(underlined);
            return this;
        }

        public Builder setObfuscated(boolean obfuscated) {
            style.setObfuscated(obfuscated);
            return this;
        }

        public Builder setClickEvent(ClickEvent.Action action, String value) {
            style.setChatClickEvent(new ClickEvent(action, value));
            return this;
        }

        public Builder setHoverEvent(String value) {
            return this.setHoverEvent(new ChatComponentText(value));
        }

        public Builder setHoverEvent(IChatComponent value) {
            return this.setHoverEvent(HoverEvent.Action.SHOW_TEXT, value);
        }

        public Builder setHoverEvent(HoverEvent.Action action, IChatComponent value) {
            style.setChatHoverEvent(new HoverEvent(action, value));
            return this;
        }

        public Builder setInsertion(String insertion) {
            style.setInsertion(insertion);
            return this;
        }

        public Builder append(String text) {
            return this.append(text, Inheritance.SHALLOW);
        }

        public Builder append(String text, Inheritance inheritance) {
            return new Builder(text, this.build(), inheritance);
        }

        public IChatComponent build() {
            IChatComponent thisComponent = new ChatComponentText(text).setChatStyle(style);
            return parent != null ? parent.appendSibling(thisComponent) : thisComponent;
        }

        public NBTTagCompound nbt() {
            return convertToNBT(this.build());
        }

        public void send() {
            ChatUtil.raw(this.build());
        }

        public void send(int chatLine) {
            mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(this.build(), chatLine);
        }

        public enum Inheritance {
            DEEP, SHALLOW, NONE
        }

    }

}
