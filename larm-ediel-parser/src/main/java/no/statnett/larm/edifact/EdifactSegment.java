package no.statnett.larm.edifact;

import java.util.List;

public class EdifactSegment {

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
        return elements;
    }

    public String getElementComponent(int elementIndex, int componentIndex) {
        return getDataElements().get(elementIndex).getComponentData().get(componentIndex);
    }

    public String getElementData(int elementIndex) {
        return getDataElements().get(elementIndex).toString();
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setDataElements(List<EdifactDataElement> elements) {
        this.elements = elements;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }
}
