package no.statnett.larm.edifact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EdifactSegment {

    ParserContext ctx;
    int lineNum, columnNum, segmentNum;
    String segmentBody;

    private List<EdifactDataElement> elements;
    private String segmentName;

    public EdifactSegment() {
    }

    EdifactSegment(String segmentName, String segmentBody) {
        this.segmentName = segmentName;
        this.segmentBody = segmentBody;
    }

    public Integer getAsInt(int elementIndex, int componentIndex) {
        EdifactDataElement element = getDataElement(elementIndex);
        return (element != null) ? element.getAsInt(componentIndex) : null;
    }

    public String getAsString(int elementIndex) {
        return getDataElements().get(elementIndex).toString();
    }

    public String getAsString(int elementIndex, int componentIndex) {
        EdifactDataElement element = getDataElement(elementIndex);
        return (element != null) ? element.getAsString(componentIndex) : null;
    }

    protected EdifactDataElement getDataElement(int index) {
        List<EdifactDataElement> lst = getDataElements();
        return (index < lst.size()) ? lst.get(index) : null;
    }

    public List<EdifactDataElement> getDataElements() {
        if (elements == null) {
            return Collections.emptyList();
        }
        return elements;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setDataElements(List<EdifactDataElement> elements) {
        this.elements = elements != null ? elements : new ArrayList<EdifactDataElement>(2);
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }
}
