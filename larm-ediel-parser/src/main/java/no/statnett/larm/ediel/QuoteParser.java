package no.statnett.larm.ediel;

import java.io.IOException;
import java.io.Reader;

import no.statnett.larm.edifact.EdifactParser;
import no.statnett.larm.edifact.SegmentSource;

public class QuoteParser {

    private SegmentSource edifactParser;

    public QuoteParser(Reader reader) throws IOException {
        this.edifactParser = new EdifactParser(reader);
    }

    public QuoteMessage parseMessage() throws IOException {
        edifactParser.readOptionalSegment("UNA");
        edifactParser.readMandatorySegment("UNB");
        edifactParser.readMandatorySegment("UNH");

        QuoteMessage message = new QuoteMessage();
        message.readSegmentGroup(edifactParser);
        edifactParser.readMandatorySegment("UNS");
        edifactParser.readMandatorySegment("CNT");
        edifactParser.readMandatorySegment("CNT");
        edifactParser.readMandatorySegment("UNT");
        edifactParser.readMandatorySegment("UNZ");
        return message;
    }

}
