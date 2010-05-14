package no.statnett.larm.edifact;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EdifactSegment {

    ParserContext ctx;
    int lineNum, columnNum, segmentNum;
    String segmentBody;

    private List<EdifactDataElement> elements;
    private String segmentName;

    public EdifactSegment() {
    }

    public EdifactSegment(String segmentName, String segmentBody) {
        this.segmentName = segmentName;
        this.segmentBody = segmentBody;
    }

    List<EdifactDataElement> getDataElements() {
        if (elements == null) {
            elements = new ArrayList<EdifactDataElement>();
        }
        return elements;
    }

    public String getElementComponent(int elementIndex, int componentIndex) {
        return getDataElements().get(elementIndex).getComponentData().get(
                componentIndex);
    }

    public void setElementComponent(int elementIndex, int componentIndex, String data) {
        while (getDataElements().size() <= elementIndex) {
            getDataElements().add(new EdifactDataElement());
        }
        List<String> componentData = getDataElements().get(elementIndex).getComponentData();
        while (componentData.size() <= componentIndex) {
            componentData.add("");
        }
        componentData.set(componentIndex, data);
        getDataElements().get(elementIndex).update(":");
        // TODO: Use proper component separator
    }

    protected EdifactDataElement getDataElement(int index) {
        List<EdifactDataElement> lst = getDataElements();
        return (index < lst.size()) ? lst.get(index) : null;
    }

    public String getElementData(int index) {
        EdifactDataElement dataElement = getDataElement(index);
        return dataElement == null ? null : dataElement.toString();
    }

    public String getSegmentName() {
        if (segmentName == null) {
            Segment annotation = getClass().getAnnotation(Segment.class);
            if (annotation != null)
                segmentName = annotation.value();
        }
        return segmentName;
    }

    public void setDataElements(List<EdifactDataElement> elements) {
        this.elements = elements;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    public void writeTo(EdifactSegmentWriter writer) throws IOException {
        writer.writeSegment(this);
    }

    @Override
    public String toString() {
        return segmentName;
    }
}
