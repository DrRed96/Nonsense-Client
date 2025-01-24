package wtf.bhopper.nonsense;

import com.google.gson.Gson;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.ViaMCP;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import wtf.bhopper.nonsense.universe.Universe;
import wtf.bhopper.nonsense.script.ScriptManager;
import wtf.bhopper.nonsense.util.render.Fonts;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import java.io.File;

public final class Nonsense {

    public static final Nonsense INSTANCE = new Nonsense();

    public static final String NAME = "Nonsense";

    public static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON = new Gson();

    // Event Bus
    private EventBus eventBus;

    // Managers
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ComponentManager componentManager;
    private ConfigManager configManager;
    private ScriptManager scriptManager;
    private AltManager altManager;
    private Universe universe;

    // Components
    private Hud hud;
    private AntiCheat antiCheat;

    // Util
    private long startTime;
    private File dataDir;

    public void setup() {
        LOGGER.info("Loading {}", NAME);
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
        this.scriptManager = new ScriptManager();
        this.altManager = new AltManager();
        this.altManager.tryLoad();
        this.universe = new Universe();

        // Rendering initialization
        NVGHelper.init();
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

    public static ScriptManager getScriptManager() {
        return INSTANCE.scriptManager;
    }

    public static AltManager getAltManager() {
        return INSTANCE.altManager;
    }

    public static Universe getUniverse() {
        return INSTANCE.universe;
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
