package wtf.bhopper.nonsense.module.property;

import java.util.List;

public interface IPropertyContainer {

    void addProperties(AbstractProperty<?>... properties);

    List<AbstractProperty<?>> getProperties();

    String getContainerId();

    default IPropertyContainer getOwner() {
        return null;
    }

}
