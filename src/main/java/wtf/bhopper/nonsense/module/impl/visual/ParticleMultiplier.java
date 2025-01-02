package wtf.bhopper.nonsense.module.impl.visual;

import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.render.EventEmitParticles;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;

@ModuleInfo(name = "Particle Multiplier", description = "Increases the amount of particles", category = ModuleCategory.VISUAL)
public class ParticleMultiplier extends Module {

    private final NumberProperty critical = new NumberProperty("Critical", "Critical particles", 1, 1, 10, 1);
    private final NumberProperty enchantment = new NumberProperty("Enchantment", "Enchantment particles", 1, 1, 10, 1);

    public ParticleMultiplier() {
        this.autoAddProperties();
    }

    @EventLink
    public final Listener<EventEmitParticles> onEmitParticles = event -> {
        switch (event.particleType) {
            case CRIT -> event.amount = critical.getInt();
            case CRIT_MAGIC -> event.amount = enchantment.getInt();
        }
    };

}
