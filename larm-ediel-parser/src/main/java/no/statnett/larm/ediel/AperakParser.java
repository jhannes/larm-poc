package no.statnett.larm.ediel;

import java.io.IOException;
import java.io.Reader;

import no.statnett.larm.edifact.EdifactParser;
import no.statnett.larm.edifact.SegmentSource;

public class AperakParser {
    private SegmentSource edifactParser;

    public AperakParser(Reader reader) throws IOException {
        this.edifactParser = new EdifactParser(reader);
    }

    public AperakMessage parseMessage() throws IOException {
        edifactParser.readOptionalSegment("UNA");
        edifactParser.readMandatorySegment("UNB");
        edifactParser.readMandatorySegment("UNH");

        AperakMessage message = new AperakMessage();
        message.readSegmentGroup(edifactParser);
        edifactParser.readMandatorySegment("UNT");
        edifactParser.readMandatorySegment("UNZ");
        return message;
    }

}
