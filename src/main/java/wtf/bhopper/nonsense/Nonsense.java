package wtf.bhopper.nonsense;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wtf.bhopper.nonsense.command.CommandManager;
import wtf.bhopper.nonsense.config.ConfigManager;
import wtf.bhopper.nonsense.event.Event;
import wtf.bhopper.nonsense.event.bus.EventBus;
import wtf.bhopper.nonsense.gui.click.novoline.NovoGui;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleManager;
import wtf.bhopper.nonsense.module.impl.visual.ClickGui;
import wtf.bhopper.nonsense.util.minecraft.TickRate;
import wtf.bhopper.nonsense.util.render.Fonts;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import java.io.File;

public enum Nonsense {
    INSTANCE;

    public static final String NAME = "Nonsense";
    public static final String VERSION = "241116";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    // Event Bus
    private EventBus<Event> eventBus;

    // Managers
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ConfigManager configManager;

    // Render Components
    private Hud hud;

    // Util
    private File dataDir;
    private TickRate tickRate;
    private long startTime;

    public void setup() {
        LOGGER.info("Loading {} {}", NAME, VERSION);
        this.startTime = System.currentTimeMillis();

        this.dataDir = new File(Minecraft.getMinecraft().mcDataDir, NAME);
        if (!this.dataDir.exists()) {
            this.dataDir.mkdirs();
        }

        this.eventBus = new EventBus<>();
        this.moduleManager = new ModuleManager();
        this.commandManager = new CommandManager();
        this.configManager = new ConfigManager();

        NVGHelper.init();
        Fonts.init();
        this.hud = new Hud();
        module(ClickGui.class).initGuis();

        this.tickRate = new TickRate();

        this.configManager.loadDefaultConfig();
    }

    public static EventBus<Event> getEventBus() {
        return INSTANCE.eventBus;
    }

    public static ModuleManager getModuleManager() {
        return INSTANCE.moduleManager;
    }

    public static <T extends Module> T module(Class<T> clazz) {
        return INSTANCE.moduleManager.get(clazz);
    }

    public static CommandManager getCommandManager() {
        return INSTANCE.commandManager;
    }

    public static ConfigManager getConfigManager() {
        return INSTANCE.configManager;
    }

    public static Hud getHud() {
        return INSTANCE.hud;
    }

    public static File getDataDir() {
        return INSTANCE.dataDir;
    }

    public static TickRate getTickRate() {
        return INSTANCE.tickRate;
    }

    public static long getStartTime() {
        return INSTANCE.startTime;
    }

}
