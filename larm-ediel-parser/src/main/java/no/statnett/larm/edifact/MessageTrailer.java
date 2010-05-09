package no.statnett.larm.edifact;

import java.util.List;

@Segment("UNT")
public class MessageTrailer extends EdifactSegment {

    private int numberOfSegments; // M, n..10
    private String referenceNumber;// M, an..14

    public MessageTrailer() {
        setSegmentName("UNT");
    }

    public int getNumberOfSegments() {
        return numberOfSegments;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    @Override
    public void setDataElements(List<EdifactDataElement> elements) {
        super.setDataElements(elements);
    }

    public void setNumberOfSegments(int numberOfSegments) {
        this.numberOfSegments = numberOfSegments;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
}
