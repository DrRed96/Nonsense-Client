package wtf.bhopper.nonsense.module;

import com.google.common.collect.ImmutableClassToInstanceMap;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventKeyPress;
import wtf.bhopper.nonsense.module.impl.combat.Criticals;
import wtf.bhopper.nonsense.module.impl.combat.KillAura;
import wtf.bhopper.nonsense.module.impl.combat.NoClickDelay;
import wtf.bhopper.nonsense.module.impl.combat.Velocity;
import wtf.bhopper.nonsense.module.impl.exploit.ServerLagger;
import wtf.bhopper.nonsense.module.impl.movement.*;
import wtf.bhopper.nonsense.module.impl.other.PackSpoofer;
import wtf.bhopper.nonsense.module.impl.player.NoFall;
import wtf.bhopper.nonsense.module.impl.player.NoRotate;
import wtf.bhopper.nonsense.module.impl.movement.Scaffold;
import wtf.bhopper.nonsense.module.impl.visual.Capes;
import wtf.bhopper.nonsense.module.impl.visual.ClickGui;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;
import wtf.bhopper.nonsense.module.impl.visual.ItemAnimations;

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
                new Velocity(),
                new Criticals(),
                new NoClickDelay(),

                // Movement
                new Sprint(),
                new NoSlow(),
                new Scaffold(),
                new Flight(),
                new Speed(),
                new MovementFix(),

                // Player
                new NoFall(),
                new NoRotate(),

                // Exploit
                new ServerLagger(),

                // Other
                new PackSpoofer(),

                // Visual
                new HudMod(),
                new ClickGui(),
                new ItemAnimations(),
                new Capes()
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

}
