package wtf.bhopper.nonsense;

import com.google.gson.Gson;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.ViaMCP;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import wtf.bhopper.nonsense.alt.AltManager;
import wtf.bhopper.nonsense.anticheat.AntiCheat;
import wtf.bhopper.nonsense.command.CommandManager;
import wtf.bhopper.nonsense.component.Component;
import wtf.bhopper.nonsense.component.ComponentManager;
import wtf.bhopper.nonsense.config.ConfigManager;
import wtf.bhopper.nonsense.event.EventBus;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleManager;
import wtf.bhopper.nonsense.module.impl.visual.ClickGui;
import wtf.bhopper.nonsense.network.NonsenseNetHandler;
import wtf.bhopper.nonsense.util.render.Fonts;
import wtf.bhopper.nonsense.util.render.ImGuiHelper;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import java.io.File;

public enum Nonsense {
    INSTANCE;

    public static final String NAME = "Nonsense";
    public static final String VERSION = "241116";

    public static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON = new Gson();

    // Event Bus
    private EventBus eventBus;

    // Managers
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ComponentManager componentManager;
    private ConfigManager configManager;
    private AltManager altManager;
    private NonsenseNetHandler netHandler;

    // Components
    private Hud hud;
    private AntiCheat antiCheat;

    // Util
    private long startTime;
    private File dataDir;

    public void setup() {
        LOGGER.info("Loading {} {}", NAME, VERSION);
        this.startTime = System.currentTimeMillis();

        ViaMCP.create();
        ViaMCP.INSTANCE.initAsyncSlider();
        ViaLoadingBase.getInstance().reload(ProtocolVersion.v1_8);

        this.dataDir = new File(Minecraft.getMinecraft().mcDataDir, NAME);
        if (!this.dataDir.exists()) {
            if (this.dataDir.mkdirs()) {
                throw new RuntimeException("Failed to create data directory: " + this.dataDir);
            }
        }

        // Managers initialization
        this.eventBus = new EventBus();
        this.moduleManager = new ModuleManager();
        this.commandManager = new CommandManager();
        this.componentManager = new ComponentManager();
        this.configManager = new ConfigManager();
        this.altManager = new AltManager();
        this.altManager.tryLoad();
        this.netHandler = new NonsenseNetHandler();

        // Rendering initialization
        NVGHelper.init();
        ImGuiHelper.init(Display.getHandle());
        Fonts.init();

        // Components initialization
        this.antiCheat = new AntiCheat();
        this.hud = new Hud();
        module(ClickGui.class).initGuis();

        this.configManager.loadDefaultConfig();
    }

    public static EventBus getEventBus() {
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

    public static ComponentManager getComponentManager() {
        return INSTANCE.componentManager;
    }

    public static <T extends Component> T component(Class<T> clazz) {
        return INSTANCE.componentManager.get(clazz);
    }

    public static ConfigManager getConfigManager() {
        return INSTANCE.configManager;
    }

    public static AltManager getAltManager() {
        return INSTANCE.altManager;
    }

    public static NonsenseNetHandler getNetHandler() {
        return INSTANCE.netHandler;
    }

    public static Hud getHud() {
        return INSTANCE.hud;
    }

    public static AntiCheat getAntiCheat() {
        return INSTANCE.antiCheat;
    }

    public static long getStartTime() {
        return INSTANCE.startTime;
    }

    public static File getDataDir() {
        return INSTANCE.dataDir;
    }

}
