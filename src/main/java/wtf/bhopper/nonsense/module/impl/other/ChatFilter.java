package wtf.bhopper.nonsense.module.impl.other;

import net.minecraft.client.resources.IResource;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ResourceLocation;
import wtf.bhopper.nonsense.command.CommandManager;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.packet.EventSendPacket;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "Chat Filter",
        description = "Allows you to bypass chat filters",
        category = ModuleCategory.OTHER,
        searchAlias = "Chat Bypass")
public class ChatFilter extends AbstractModule {

    private static final List<String> COMMANDS = Arrays.asList("message", "msg", "whisper", "w", "tell", "reply", "r");
    private static final List<String> HYPIXEL_COMMANDS = Arrays.asList(
            "allchat", "achat", "ac",
            "partychat", "pchat", "pc",
            "guildchat", "gchat", "gc",
            "officerchat", "ochat", "oc",
            "coopchat", "cchat", "cc",
            "shout"
    );

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Chat filter bypass method.", Mode.BYPASS);
    private final BooleanProperty wordCheck = new BooleanProperty("Word Check", "Check for specific words before filtering.", true);
    private final NumberProperty frequency = new NumberProperty("Frequency", "Invisible character frequency.", () -> this.mode.is(Mode.BYPASS), 2, 1, 5, 1);

    private final List<String> words = new ArrayList<>();

    public ChatFilter() {
        super();
        this.addProperties(this.mode, this.wordCheck, this.frequency);
        this.setSuffix(this.mode::getDisplayValue);

        try {
            IResource resource = mc.getResourceManager().getResource(new ResourceLocation("nonsense/chatfilter.txt"));

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                while (reader.ready()) {
                    this.words.add(reader.readLine());
                }
            }

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @EventLink
    public final Listener<EventSendPacket> onSendPacket = event -> {

        if (event.packet instanceof C01PacketChatMessage packet) {

            if (packet.message.startsWith(CommandManager.PREFIX)) {
                return;
            }

            String[] words = packet.message.split("\\s+");
            for (int i = 0; i < words.length; i++) {

                if (i == 0 && words[0].startsWith("/")) {
                    if (COMMANDS.contains(words[0].toLowerCase().substring(1))) {
                        continue;
                    } else {
                        return;
                    }
                }

                words[i] = switch (this.mode.get()) {
                    case BYPASS -> this.applyBypass(words[i]);
                    case ACCENT -> this.applyAccent(words[i]);
                    case MINIBLOX -> this.applyMiniblox(words[i]);
                };
            }

            packet.message = String.join(" ", words);
        }
    };

    private String applyBypass(String word) {
        if (!this.wordCheck.get() || this.isBadWord(word) || this.isLink(word)) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < word.length(); i++) {
                builder.append(word.charAt(i));
                if (i % this.frequency.getInt() == 0) {
                    // U+05FD is an invalid unicode character so it appears invisible in chat
                    // https://en.wikipedia.org/wiki/List_of_Unicode_characters#Semitic_languages
                    builder.append('\u05FD');
                }
            }
            return builder.toString();
        }

        return word;
    }

    private String applyAccent(String word) {

        String result = word;

        if (this.isLink(result)) {
            result = result.replace('.', '\u2025');
        }

        if (this.isBadWord(result) || !this.wordCheck.get()) {
            boolean found = false;
            StringBuilder builder = new StringBuilder();
            for (char c : result.toCharArray()) {
                if (!found) {
                    char toAppend = switch (c) {
                        case 'a' -> '\u00E0';
                        case 'A' -> '\u00C4';
                        case 'e' -> '\u00E8';
                        case 'E' -> '\u00C9';
                        case 'i', 'I' -> '\u00A1';
                        case 'o' -> '\u00F2';
                        case 'O' -> '\u00D8';
                        case 'u' -> '\u00F9';
                        case 'U' -> '\u00DC';
                        default -> '\0';
                    };
                    if (toAppend == '\0') {
                        builder.append(c);
                    } else {
                        builder.append(toAppend);
                        found = true;
                    }
                } else {
                    builder.append(c);
                }
            }

            if (!found) {
                builder.append('\u02CC');
            }

            return builder.toString();
        }

        return result;
    }

    private String applyMiniblox(String word) {
        if (!this.wordCheck.get() || this.isBadWord(word) || this.isLink(word)) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < word.length(); i++) {
                builder.append(word.charAt(i));
                if (i % this.frequency.getInt() == 0) {
                    builder.append('\u200D');
                }
            }
            return builder.toString();
        }

        return word;
    }

    private boolean isBadWord(String word) {
        String lowerCase = word.toLowerCase();
        for (String badWord : this.words) {
            if (lowerCase.contains(badWord)) {
                return true;
            }
        }

        return false;
    }

    private boolean isLink(String word) {
        return GeneralUtil.LINK_REGEX.matcher(word).find() || word.toLowerCase().startsWith("discord.gg");
    }

    private enum Mode {
        BYPASS,
        ACCENT,
        MINIBLOX
    }

}
