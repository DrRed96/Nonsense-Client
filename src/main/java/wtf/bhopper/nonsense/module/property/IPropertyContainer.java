package wtf.bhopper.nonsense.module.property;

import java.util.List;

public interface IPropertyContainer {

    void addProperties(Property<?>... properties);

    List<Property<?>> getProperties();

    String getContainerId();

    default IPropertyContainer getOwner() {
        return null;
    }

}
