package wtf.bhopper.nonsense.module;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.impl.combat.*;
import wtf.bhopper.nonsense.module.impl.exploit.PingSpoofer;
import wtf.bhopper.nonsense.module.impl.exploit.ServerLagger;
import wtf.bhopper.nonsense.module.impl.movement.*;
import wtf.bhopper.nonsense.module.impl.other.ChatFilter;
import wtf.bhopper.nonsense.module.impl.other.Debugger;
import wtf.bhopper.nonsense.module.impl.other.Derp;
import wtf.bhopper.nonsense.module.impl.other.Panic;
import wtf.bhopper.nonsense.module.impl.player.*;
import wtf.bhopper.nonsense.module.impl.visual.ClickGuiMod;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;
import wtf.bhopper.nonsense.module.impl.visual.ItemAnimations;
import wtf.bhopper.nonsense.module.impl.visual.NoRender;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager extends LinkedHashMap<Class<? extends Module>, Module> {

    public void addModules() {

        // Combat
        this.addModule(new KillAura());
        this.addModule(new AutoBlock());
        this.addModule(new AntiBot());
        this.addModule(new Velocity());
        this.addModule(new NoClickDelay());

        // Movement
        this.addModule(new Sprint());
        this.addModule(new NoSlow());
        this.addModule(new Speed());
        this.addModule(new Flight());
        this.addModule(new MovementFix());

        // Player
        this.addModule(new Scaffold());
        this.addModule(new NoFall());
        this.addModule(new GameSpeed());
        this.addModule(new NoRotate());
        this.addModule(new AutoRespawn());

        // Exploit
        this.addModule(new PingSpoofer());
        this.addModule(new ServerLagger());

        // Other
        this.addModule(new ChatFilter());
        this.addModule(new Derp());
        this.addModule(new Panic());
        this.addModule(new Debugger());

        // Visual
        this.addModule(new HudMod());
        this.addModule(new ClickGuiMod());
        this.addModule(new NoRender());
        this.addModule(new ItemAnimations());
    }

    public void addModule(Module module) {
        Nonsense.LOGGER.info("Add module: {}", module.name);
        this.put(module.getClass(), module);
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> clazz) {
        return (T)this.getOrDefault(clazz, null);
    }

    public Module get(String name) {
        return this.values()
                .stream()
                .filter(module -> module.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public boolean isEnabled(Class<? extends Module> clazz) {
        try {
            return this.getOrDefault(clazz, null).isEnabled();
        } catch (NullPointerException ignored) {}

        return false;
    }

    public List<Module> getInCategory(Module.Category category) {
        return this.values()
                .stream()
                .filter(module -> module.category == category)
                .collect(Collectors.toList());
    }

}
