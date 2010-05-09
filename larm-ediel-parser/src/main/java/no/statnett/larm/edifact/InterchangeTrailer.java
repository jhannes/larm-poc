package no.statnett.larm.edifact;

import java.util.List;

@Segment("UNZ")
public class InterchangeTrailer extends EdifactSegment {

    private Integer controlCount; // M, n..6
    private String controlReference;// M, an..14

    public InterchangeTrailer() {
        setSegmentName("UNZ");
    }

    public Integer getControlCount() {
        return controlCount;
    }

    public String getControlReference() {
        return controlReference;
    }

    public void setControlCount(int controlCount) {
        this.controlCount = controlCount;
    }

    public void setControlReference(String controlReference) {
        this.controlReference = controlReference;
    }

    @Override
    public void setDataElements(List<EdifactDataElement> elements) {
        super.setDataElements(elements);
        controlCount = getAsInt(0, 0);
        controlReference = getAsString(1, 0);
    }
}
