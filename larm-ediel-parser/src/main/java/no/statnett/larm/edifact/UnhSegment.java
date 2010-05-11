package no.statnett.larm.edifact;

public class UnhSegment extends EdifactSegment {

    public UnhSegment() {
        setSegmentName("UNH");
    }

    public UnhSegment(String messageType, String version, String release, String agency, String associatedCode) {
        this();
        setElementComponent(0, 0, "1");
        setElementComponent(1, 0, messageType);
        setElementComponent(1, 1, version);
        setElementComponent(1, 2, release);
        setElementComponent(1, 3, agency);
        setElementComponent(1, 4, associatedCode);
    }

}
