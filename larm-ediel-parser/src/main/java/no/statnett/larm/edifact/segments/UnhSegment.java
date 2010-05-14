package no.statnett.larm.edifact.segments;

import no.statnett.larm.edifact.EdifactMessage;
import no.statnett.larm.edifact.EdifactSegment;

public class UnhSegment extends EdifactSegment {

    public UnhSegment() {
        setSegmentName("UNH");
    }

    public UnhSegment(EdifactMessage message) {
        this();
        setElementComponent(0, 0, "1");
        setElementComponent(1, 0, message.getMessageType());
        setElementComponent(1, 1, message.getVersion());
        setElementComponent(1, 2, message.getRelease());
        setElementComponent(1, 3, message.getAgency());
        setElementComponent(1, 4, message.getAssociatedCode());
    }
}
