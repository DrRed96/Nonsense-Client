package wtf.bhopper.nonsense.module;

import com.google.common.collect.ImmutableClassToInstanceMap;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventKeyPress;
import wtf.bhopper.nonsense.module.impl.combat.*;
import wtf.bhopper.nonsense.module.impl.exploit.Disabler;
import wtf.bhopper.nonsense.module.impl.exploit.PingSpoofer;
import wtf.bhopper.nonsense.module.impl.exploit.Plugins;
import wtf.bhopper.nonsense.module.impl.exploit.ServerLagger;
import wtf.bhopper.nonsense.module.impl.movement.*;
import wtf.bhopper.nonsense.module.impl.other.*;
import wtf.bhopper.nonsense.module.impl.player.AutoRespawn;
import wtf.bhopper.nonsense.module.impl.player.GameSpeed;
import wtf.bhopper.nonsense.module.impl.player.NoFall;
import wtf.bhopper.nonsense.module.impl.player.NoRotate;
import wtf.bhopper.nonsense.module.impl.movement.Scaffold;
import wtf.bhopper.nonsense.module.impl.visual.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {

    private final ImmutableClassToInstanceMap<Module> modules;

    public ModuleManager() {
        this.modules = this.addModules(
                // Combat
                new KillAura(),
                new AntiBot(),
                new Velocity(),
                new AutoBlock(),
                new Criticals(),
                new NoClickDelay(),

                // Movement
                new Sprint(),
                new NoSlow(),
                new Scaffold(),
                new Flight(),
                new Speed(),
                new Step(),
                new AntiFall(),
                new InventoryMove(),
                new Jesus(),
                new Phase(),
                new Strafe(),
                new MovementFix(),

                // Player
                new NoFall(),
                new NoRotate(),
                new AutoRespawn(),
                new GameSpeed(),

                // Exploit
                new PingSpoofer(),
                new Disabler(),
                new ServerLagger(),
                new Plugins(),

                // Other
                new AntiAim(),
                new AntiDesync(),
                new PackSpoofer(),
                new AutoSpeedBuilders(),
                new LightningDetector(),
                new DiscordRPCMod(),
                new Debugger(),

                new HudMod(),
                // Visual
                new ClickGui(),
                new Esp(),
                new FullBright(),
                new BlockOverlay(),
                new ItemAnimations(),
                new NoRender(),
                new LagNotifier(),
                new Capes(),
                new ItemPhysics(),
                new BarrierView()
        );

        Nonsense.getEventBus().subscribe(this);
    }

    @EventLink
    public final Listener<EventKeyPress> onKeyPress = event -> {
        for (final Module module : this.getModules()) {
            if (module.getBind() == event.key) {
                module.toggle();
            }
        }
    };

    @SuppressWarnings("unchecked")
    private ImmutableClassToInstanceMap<Module> addModules(Module... modules) {
        ImmutableClassToInstanceMap.Builder<Module> modulesBuilder = ImmutableClassToInstanceMap.builder();
        Arrays.stream(modules).forEach(module -> modulesBuilder.put((Class<Module>)module.getClass(), module));
        return modulesBuilder.build();
    }

    public Collection<Module> getModules() {
        return this.modules.values();
    }

    public <T extends Module> T get(Class<T> clazz) {
        return this.modules.getInstance(clazz);
    }

    public Module get(String name) {
        return this.getModules()
                .stream()
                .filter(module -> module.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<Module> getInCategory(ModuleCategory category) {
        return this.getModules()
                .stream()
                .filter(module -> module.category == category)
                .collect(Collectors.toList());
    }

    public int size() {
        return this.modules.size();
    }

    public int amountEnabled() {
        return (int)this.getModules()
                .stream()
                .filter(Module::isToggled)
                .count();
    }

}
