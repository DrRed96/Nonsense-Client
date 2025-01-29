package wtf.bhopper.nonsense.module.impl.other;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.IResource;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventJoinGame;
import wtf.bhopper.nonsense.event.impl.packet.EventReceivePacket;
import wtf.bhopper.nonsense.event.impl.render.EventRenderGui;
import wtf.bhopper.nonsense.gui.components.RenderComponent;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.RenderUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "Guess The Build Solver",
        description = "Attempts to solve Guess The Build builds",
        category = ModuleCategory.OTHER)
public class GuessTheBuildSolver extends AbstractModule {

    private static final String[] RANK_PREFIX = {
            "Rookie",
            "Untrained",
            "Amateur",
            "Prospect",
            "Apprentice",
            "Experienced",
            "Seasoned",
            "Trained",
            "Skilled",
            "Talented",
            "Professional",
            "Artisan",
            "Expert",
            "Master",
            "Legend",
            "Grandmaster",
            "Celestial",
            "Divine",
            "Ascended",
            "#1 Builder",
            "#2 Builder",
            "#3 Builder",
            "#4 Builder",
            "#5 Builder",
            "#6 Builder",
            "#7 Builder",
            "#8 Builder",
            "#9 Builder",
            "#10 Builder"
    };

    private final BooleanProperty autoGuess = new BooleanProperty("Auto Guess", "Automatically guesses when there's only 1 theme left", true);
    private final NumberProperty delay = new NumberProperty("Guess Delay", "Auto guess delay", this.autoGuess::get, 0, 0, 3000, 50, NumberProperty.FORMAT_MS);

    private final List<String> themes = new ArrayList<>();
    private final List<String> possibleWords = new ArrayList<>();
    private final List<String> impossibleWords = new ArrayList<>();

    private boolean guessed = false;
    private String lastGuess = "";

    private final Render render = new Render();

    public GuessTheBuildSolver() {
        super();
        this.addProperties(this.autoGuess, this.delay);
        this.addProperties(this.render.getProperties());
        this.loadThemes();
    }

    @Override
    public void onDisable() {
        this.render.setEnabled(false);
    }

    @EventLink
    public final Listener<EventReceivePacket> onReceivePacket = event -> {
        if (event.packet instanceof S02PacketChat packet) {

            String message = EnumChatFormatting.getTextWithoutFormattingCodes(packet.getChatComponent().getUnformattedText());

            if (packet.getType() == 2) {
                if (message.startsWith("The theme is ")) {
                    String word = message.replace("The theme is ", "");
                    if (!lastGuess.equalsIgnoreCase(word)) {
                        lastGuess = word;
                        try {
                            possibleWords.clear();
                            for (String value : this.themes) {
                                String guessWord = value.toLowerCase();
                                if (word.length() != guessWord.length()) continue;

                                char[] guessChars = guessWord.toCharArray();
                                char[] wordChars = word.toLowerCase().replace("_", ".").toCharArray();
                                boolean goodWord = true;
                                for (int i = 0; i < guessChars.length; i++) {
                                    if (wordChars[i] == '.') continue;
                                    if (guessChars[i] != wordChars[i]) {
                                        goodWord = false;
                                        break;
                                    }
                                }
                                if (goodWord) possibleWords.add(value);
                            }
                            possibleWords.removeIf(e -> this.possibleWords.contains(e.toLowerCase()));
                            if (possibleWords.size() == 1 && !this.guessed && this.autoGuess.get()) {
                                try {
                                    if (this.delay.getInt() == 0) {
                                        ChatUtil.sendNoEvent("%s", possibleWords.getFirst());
                                    } else {
                                        new Thread(() -> {
                                            try {
                                                Thread.sleep(this.delay.getInt());
                                                ChatUtil.sendNoEvent("%s", possibleWords.getFirst());
                                            } catch (Exception ignored) {}
                                        }).start();
                                    }
                                } catch (Exception ignored) {}
                                Notification.send("Guess The Build", "Theme has been identified as \247a" + possibleWords.getFirst(), NotificationType.SUCCESS, 3000);
                            }
                        } catch (Exception ignored) {
                        }
                    }

                }
            } else {

                // Checking is you've guessed the theme or it's your turn
                if (message.equals(mc.thePlayer.getName() + " correctly guessed the theme!") || message.equals("You can't send messages when it's your turn to build!")) {
                    guessed = true;
                }

                // New round
                if (message.startsWith("Round: ")) {
                    guessed = false;
                    possibleWords.clear();
                    impossibleWords.clear();
                }

                // Player guessed words
                for (String prefix : RANK_PREFIX) {
                    if (message.startsWith(prefix)) {
                        this.addImpossible(message.split(": ")[1].toLowerCase());
                        break;
                    }
                }

            }

        }
    };

    @EventLink
    public final Listener<EventRenderGui> onRender = event -> this.render.setEnabled(true);

    @EventLink
    public final Listener<EventJoinGame> onJoin = event -> {
        this.possibleWords.clear();
        this.impossibleWords.clear();
        this.guessed = false;
    };

    private void addImpossible(String word) {
        this.impossibleWords.add(word);
        this.possibleWords.removeIf(str -> impossibleWords.contains(str.toLowerCase()));
        if (this.possibleWords.size() == 1 && !guessed && this.autoGuess.get()) {
            ChatUtil.send("%s", possibleWords.getFirst());
        }
    }

    private void loadThemes() {
        try {
            IResource resource = mc.getResourceManager().getResource(new ResourceLocation("nonsense/gtb.txt"));

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                while (reader.ready()) {
                    String line = reader.readLine();
                    if (line.startsWith("//")) {
                        continue;
                    }
                    this.themes.add(line);
                }
            }

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public class Render extends RenderComponent {

        public Render() {
            super("GTB", 2, 22, mc.fontRendererObj.getStringWidth("____________________") * 2, mc.fontRendererObj.FONT_HEIGHT + 2);
        }

        @Override
        public void draw(float delta, int mouseX, int mouseY, boolean bypass) {
            FontRenderer font = mc.fontRendererObj;
            int count = 1;

            this.setHeight(6 + (possibleWords.size() + 1) * font.FONT_HEIGHT * 2);
            this.drawBackground(0x80000000);

            RenderUtil.drawScaledString("Possible Themes \2478(\2477" + GuessTheBuildSolver.this.possibleWords.size() + "\2478)", 4, 4, Hud.color(), true, 2.0F);

            int index = mouseY / (mc.fontRendererObj.FONT_HEIGHT * 2) + 1;

            try {
                for (String word : GuessTheBuildSolver.this.possibleWords) {
                    RenderUtil.drawScaledString(count + ": ", 4, 4 + count * font.FONT_HEIGHT * 2.0F, Hud.color(), true, 2.0F);
                    RenderUtil.drawScaledString(word, 32, 4 + count * font.FONT_HEIGHT * 2.0F,  index == count ? Hud.color() : ColorUtil.WHITE, true, 2.0F);
                    count++;
                }
            } catch (Exception ignored) {}

        }

        @Override
        public void onClick(int x, int y, int button) {
            if (this.mouseIntersecting(x, y)) {
                int index = y / (mc.fontRendererObj.FONT_HEIGHT * 2);
                try {
                    ChatUtil.sendNoEvent(GuessTheBuildSolver.this.possibleWords.get(index));
                } catch (IndexOutOfBoundsException ignored) {}
            }
        }
    }

}
