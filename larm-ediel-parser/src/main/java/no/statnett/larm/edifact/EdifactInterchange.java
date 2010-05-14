package no.statnett.larm.edifact;

import java.io.IOException;
import java.util.Random;

import no.statnett.larm.edifact.segments.UnaSegment;
import no.statnett.larm.edifact.segments.UnbSegment;
import no.statnett.larm.edifact.segments.UnhSegment;
import no.statnett.larm.edifact.segments.UntSegment;
import no.statnett.larm.edifact.segments.UnzSegment;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;


public class EdifactInterchange {

    private EdifactMessage message;
    private UnbSegment unbSegment = new UnbSegment();

    public EdifactInterchange(EdifactMessage message) {
        this.message = message;
        this.unbSegment.setTimestamp(new DateTime());
        this.unbSegment.setControlReference(getDefaultControlReference());
    }

    public EdifactInterchange() {
        // TODO Auto-generated constructor stub
    }

    private String getDefaultControlReference() {
        Random random = new Random();
        return new DateMidnight().toString("yyyyMMdd") +
            leftPad(String.valueOf(random.nextInt(100000)), '0', 6);
    }

    private String leftPad(String string, char paddingChar, int length) {
        StringBuilder result = new StringBuilder();
        for (int i=0; i<(length-string.length()); i++) result.append(paddingChar);
        result.append(string);
        return result.toString();
    }

    public void writeTo(Appendable writer) throws IOException {
        new UnaSegment(":+.? '").write(writer);

        EdifactSegmentWriter segmentWriter = new EdifactSegmentWriter(writer);
        unbSegment.writeTo(segmentWriter);
        new UnhSegment(message).writeTo(segmentWriter);

        segmentWriter.resetSegmentCount();
        message.write(segmentWriter);

        new UntSegment(String.valueOf(segmentWriter.getSegmentCount()+2), "1").writeTo(segmentWriter); // TODO: Must count segments!
        new UnzSegment("1", unbSegment.getControlReference()).writeTo(segmentWriter);
    }

    public void setSyntax(String syntax, String version) {
        this.unbSegment.setSyntax(syntax, version);
    }

    public void setSender(String partyId, String qualifier, String internalId) {
        this.unbSegment.setSender(partyId, qualifier, internalId);
    }

    public void setRecipient(String partyId, String qualifier, String internalId) {
        this.unbSegment.setRecipient(partyId, qualifier, internalId);
    }

    public String getControlReference() {
        return unbSegment.getControlReference();
    }

    public void setControlReference(String reference) {
        this.unbSegment.setControlReference(reference);
    }

    public void setUnbSegment(UnbSegment unbSegment) {
        this.unbSegment = unbSegment;
    }

    public EdifactMessage getMessage() {
        return message;
    }

    public void setMessage(EdifactMessage message) {
        this.message = message;
    }

}
