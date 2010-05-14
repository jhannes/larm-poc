package no.statnett.larm.edifact;

import org.joda.time.DateTime;

public class UnbSegment extends EdifactSegment {

    public UnbSegment() {
        setSegmentName("UNB");
    }

    public void setSyntax(String syntax, String version) {
        setElementComponent(0, 0, syntax);
        setElementComponent(0, 1, version);
    }

    public void setSender(String partyId, String qualifier, String internalId) {
        setElementComponent(1, 0, partyId);
        setElementComponent(1, 1, qualifier);
        if (!isBlank(internalId)) setElementComponent(1, 2, internalId);
    }

    public void setRecipient(String partyId, String qualifier, String internalId) {
        setElementComponent(2, 0, partyId);
        setElementComponent(2, 1, qualifier);
        if (!isBlank(internalId)) setElementComponent(2, 2, internalId);
    }


    private boolean isBlank(String string) {
        return string == null || string.length() == 0;
    }

    public void setTimestamp(DateTime dateTime) {
        setElementComponent(3, 0, dateTime.toString("yyMMdd"));
        setElementComponent(3, 1, dateTime.toString("HHmm"));
    }

    public String getControlReference() {
        return getElementComponent(4, 0);
    }

    public void setControlReference(String reference) {
        setElementComponent(4, 0, reference);
    }

}
