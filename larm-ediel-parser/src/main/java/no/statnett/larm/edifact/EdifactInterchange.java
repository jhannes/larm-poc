package no.statnett.larm.edifact;

import java.io.IOException;


public class EdifactInterchange {

    private final EdifactMessage message;

    public EdifactInterchange(EdifactMessage message) {
        this.message = message;
    }

    public void write(Appendable writer) throws IOException {
        new UnaSegment(":+.? '").write(writer);
        new UnbSegment().write(writer);
        new UnhSegment("APERAK", "D", "96A", "UN", "EDIEL2").write(writer);

        message.write(writer);

        new UntSegment("7", "1").write(writer); // TODO: Must count segments!
        new UnzSegment("1", "29").write(writer);
    }

    public void setSyntax(String identifier, String version) {
    }

    public void setSender(String partyId, String qualifier, String internalId) {
    }

    public void setRecipient(String partyId, String qualifier, String internalId) {
    }

    public void setReference(String reference) {
    }
}
