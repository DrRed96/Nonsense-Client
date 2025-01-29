package wtf.bhopper.nonsense.module;

import com.google.common.collect.ImmutableClassToInstanceMap;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventKeyPress;
import wtf.bhopper.nonsense.module.impl.combat.*;
import wtf.bhopper.nonsense.module.impl.exploit.*;
import wtf.bhopper.nonsense.module.impl.movement.*;
import wtf.bhopper.nonsense.module.impl.other.*;
import wtf.bhopper.nonsense.module.impl.player.*;
import wtf.bhopper.nonsense.module.impl.visual.*;
import wtf.bhopper.nonsense.script.AbstractScriptModule;
import wtf.bhopper.nonsense.script.ScriptOptionsMod;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager {

    private final ImmutableClassToInstanceMap<AbstractModule> modules;
    private final List<AbstractScriptModule> scriptModules;

    public ModuleManager() {

        this.modules = this.createModuleMap(
                // Combat
                new KillAura(),
                new AntiBot(),
                new Velocity(),
                new AutoBlock(),
                new Criticals(),
                new AutoClicker(),
                new TargetStrafe(),
                new FastBow(),
                new InfiniteAura(),
                new NoClickDelay(),
                new AutoCopsAndCrims(),
                new AutoZombies(),

                // Movement
                new Sprint(),
                new NoSlow(),
                new Scaffold(),
                new Flight(),
                new Speed(),
                new Step(),
                new Spider(),
                new LongJump(),
                new AntiFall(),
                new InventoryMove(),
                new Jesus(),
                new Phase(),
                new Terrain(),
                new Strafe(),
                new NoJumpDelay(),
                new FastSneak(),
                new MovementFix(),

                // Player
                new NoFall(),
                new InventoryManager(),
                new NoRotate(),
                new AutoRespawn(),
                new FastUse(),
                new FastMine(),
                new FastPlace(),
                new AutoPlace(),
                new Breaker(),
                new Avoid(),
                new GameSpeed(),
                new HorseJump(),
                new Nuker(),

                // Exploit
                new PingSpoofer(),
                new Disabler(),
                new ServerLagger(),
                new ClientSpoofer(),
                new ActivatedSpawners(),
                new JoinClaim(),
                new Plugins(),
                new Popbob(),

                // My apologies, I was told not to leak it.
                // - CalculusHvH
                this.loadPrivateModule("exploit.MinibloxDisabler"),

                // Other
                new AntiCheatMod(),
                new ChatFilter(),
                new Spammer(),
                new AntiAim(),
                new AntiDesync(),
                new Announcer(),
                new PackSpoofer(),
                new AutoHypixel(),
                new PartySpammer(),
                new GuessTheBuildSolver(),
                new AutoSpeedBuilders(),
                new AutoPixelParty(),
                new LightningDetector(),
                new SkinBlinker(),
                new DiscordRPCMod(),
                new IrcMod(),
                new Debugger(),

                // Visual
                new HudMod(),
                new ClickGui(),
                new Esp(),
                new Chams(),
                new Freecam(),
                new Atmosphere(),
                new BlockOverlay(),
                new Xray(),
                new Breadcrumbs(),
                new ItemAnimations(),
                new NoRender(),
                new ScoreboardMod(),
                new BetterChat(),
                new Crosshair(),
                new Tweaks(),
                new Tracers(),
                new Trajectories(),
                new LagNotifier(),
                new Capes(),
                new ItemPhysics(),
                new ParticleMultiplier(),
                new BarrierView(),

                // Script
                new ScriptOptionsMod()
        );

        this.scriptModules = new CopyOnWriteArrayList<>();

        Nonsense.getEventBus().subscribe(this);
    }

    @EventLink
    public final Listener<EventKeyPress> onKeyPress = event -> {
        for (final AbstractModule module : this.getModules()) {
            if (module.getBind() == event.key) {
                module.toggle();
            }
        }
    };

    @SuppressWarnings("unchecked")
    private ImmutableClassToInstanceMap<AbstractModule> createModuleMap(AbstractModule... modules) {
        ImmutableClassToInstanceMap.Builder<AbstractModule> builder = ImmutableClassToInstanceMap.builder();
        Arrays.stream(modules).forEach(module -> {
            if (module != null) {
                builder.put((Class<AbstractModule>) module.getClass(), module);
            }
        });
        return builder.build();
    }

    public List<AbstractModule> getModules() {
        List<AbstractModule> modules = new ArrayList<>(this.modules.values());
        modules.addAll(this.scriptModules);
        return modules;
    }

    public <T extends AbstractModule> T get(Class<T> clazz) {
        return this.modules.getInstance(clazz);
    }

    public AbstractModule get(String name) {
        return this.getModules()
                .stream()
                .filter(module -> module.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public AbstractScriptModule getScript(String name) {
        return this.scriptModules
                .stream()
                .filter(module -> module.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<AbstractModule> getInCategory(ModuleCategory category) {
        return this.getModules()
                .stream()
                .filter(module -> module.category == category)
                .toList();
    }

    public List<AbstractModule> search(String name) {
        return this.getModules()
                .stream()
                .filter(module -> module.matches(name))
                .toList();
    }

    public int size() {
        return this.getModules().size();
    }

    public int amountEnabled() {
        return (int)this.getModules()
                .stream()
                .filter(AbstractModule::isToggled)
                .count();
    }

    public boolean addScriptModule(AbstractScriptModule module) {
        if (this.get(module.name) != null) {
            return false;
        }
        this.scriptModules.add(module);
        return true;
    }

    public void clearScriptModules() {
        this.scriptModules.clear();
    }

    private AbstractModule loadPrivateModule(String name) {
        try {
            Class<?> clazz = Class.forName(this.getClass().getPackageName() + ".impl." + name);
            return (AbstractModule)clazz.getConstructor().newInstance();
        } catch (Exception _) {
            return null;
        }
    }

}
