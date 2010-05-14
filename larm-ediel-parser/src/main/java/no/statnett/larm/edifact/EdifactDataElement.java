package no.statnett.larm.edifact;

import java.util.ArrayList;
import java.util.List;

public class EdifactDataElement {

    private final List<String> componentData;
    private String dataElement;

    public EdifactDataElement(final String dataElement, final List<String> componentData) {
        this.dataElement = dataElement;
        this.componentData = componentData;
    }

    public EdifactDataElement() {
        this.componentData = new ArrayList<String>();
    }

    public List<String> getComponentData() {
        return componentData;
    }

    @Override
    public String toString() {
        return dataElement;
    }

    public void update(String componentSeparator) {
        StringBuilder newDataElement = new StringBuilder();
        for (String component : componentData) {
            if (newDataElement.length() > 0) newDataElement.append(componentSeparator);
            newDataElement.append(component);
        }
        this.dataElement = newDataElement.toString();
    }

}
