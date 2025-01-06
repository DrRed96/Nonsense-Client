package wtf.bhopper.nonsense.component;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.component.impl.packet.*;
import wtf.bhopper.nonsense.component.impl.player.RotationsComponent;
import wtf.bhopper.nonsense.component.impl.player.SilentSlotComponent;
import wtf.bhopper.nonsense.component.impl.world.TickRateComponent;

import java.util.Arrays;

// Yes I'm stealing from Rise, get over it.

public class ComponentManager {

    private final ClassToInstanceMap<Component> components;

    public ComponentManager() {
        this.components = addComponents(
                new AntiExploitComponent(),
                new BlinkComponent(),
                new LastConnectionComponent(),
                new PingComponent(),
                new PingSpoofComponent(),
                new RotationsComponent(),
                new SilentSlotComponent(),
                new TickRateComponent()
        );

        for (Component component : this.components.values()) {
            Nonsense.getEventBus().subscribe(component);
        }
    }

    @SuppressWarnings("unchecked")
    private ClassToInstanceMap<Component> addComponents(Component... components) {
        ImmutableClassToInstanceMap.Builder<Component> builder = ImmutableClassToInstanceMap.builder();
        Arrays.stream(components).forEach(component -> builder.put((Class<Component>) component.getClass(), component));
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T get(Class<T> clazz) {
        return (T)this.components.get(clazz);
    }

}
