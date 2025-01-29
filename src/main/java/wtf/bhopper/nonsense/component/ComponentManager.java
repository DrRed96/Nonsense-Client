package wtf.bhopper.nonsense.component;

import com.google.common.collect.ImmutableClassToInstanceMap;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.component.impl.packet.*;
import wtf.bhopper.nonsense.component.impl.player.RotationsComponent;
import wtf.bhopper.nonsense.component.impl.player.SilentSlotComponent;
import wtf.bhopper.nonsense.component.impl.world.TickRateComponent;

import java.util.Arrays;
import java.util.Collection;

// Yes I'm stealing from Rise, get over it.

public class ComponentManager {

    private final ImmutableClassToInstanceMap<AbstractComponent> components;

    public ComponentManager() {
        this.components = addComponents(
                new AntiExploitComponent(),
                new BlinkComponent(),
                new InventoryExploitComponent(),
                new LastConnectionComponent(),
                new PingComponent(),
                new PingSpoofComponent(),
                new RotationsComponent(),
                new SilentSlotComponent(),
                new TickRateComponent()
        );

        for (AbstractComponent component : this.components.values()) {
            Nonsense.getEventBus().subscribe(component);
        }
    }

    @SuppressWarnings("unchecked")
    private ImmutableClassToInstanceMap<AbstractComponent> addComponents(AbstractComponent... components) {
        ImmutableClassToInstanceMap.Builder<AbstractComponent> builder = ImmutableClassToInstanceMap.builder();
        Arrays.stream(components).forEach(component -> builder.put((Class<AbstractComponent>) component.getClass(), component));
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractComponent> T get(Class<T> clazz) {
        return (T)this.components.get(clazz);
    }

    public Collection<AbstractComponent> getComponents() {
        return this.components.values();
    }

}
