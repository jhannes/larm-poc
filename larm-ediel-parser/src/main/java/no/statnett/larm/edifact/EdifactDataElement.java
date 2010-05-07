package no.statnett.larm.edifact;

import java.util.List;

public class EdifactDataElement {

    private final List<String> componentData;
    private String dataElement;

    public EdifactDataElement(final String dataElement, final List<String> componentData) {
        this.dataElement = dataElement;
        this.componentData = componentData;
    }

    public List<String> getComponentData() {
        return componentData;
    }

    @Override
    public String toString() {
        return dataElement;
    }

}
