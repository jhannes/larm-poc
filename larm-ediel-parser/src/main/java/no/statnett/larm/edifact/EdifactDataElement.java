package no.statnett.larm.edifact;

import java.util.List;

public class EdifactDataElement {

    private final List<String> componentData;
    private String token;

    public EdifactDataElement(final String dataElement, final List<String> componentData) {
        this.token = dataElement;
        this.componentData = componentData;
    }

    public List<String> getComponentData() {
        return componentData;
    }

    public String getAsString(int index) {
        if (componentData == null || index >= componentData.size()) {
            return null;
        }
        return componentData.get(index);
    }

    public Integer getAsInt(int index) {
        return Integer.valueOf(getAsString(index));
    }

    @Override
    public String toString() {
        return token;
    }
}
