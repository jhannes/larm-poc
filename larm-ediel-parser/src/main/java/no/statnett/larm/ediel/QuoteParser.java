package no.statnett.larm.ediel;

import java.io.IOException;
import java.io.Reader;

import no.statnett.larm.edifact.EdifactInterchange;
import no.statnett.larm.edifact.EdifactParser;
import no.statnett.larm.edifact.segments.UnbSegment;

public class QuoteParser {

    public EdifactInterchange parseInterchange(Reader reader) throws IOException {
        EdifactParser edifactParser = new EdifactParser(reader);
        EdifactInterchange interchange = new EdifactInterchange();

        edifactParser.readOptionalSegment("UNA");
        interchange.setUnbSegment(edifactParser.readMandatorySegment(UnbSegment.class));

        edifactParser.readMandatorySegment("UNH");

        QuoteMessage message = new QuoteMessage();
        message.readSegmentGroup(edifactParser);
        interchange.setMessage(message);

        edifactParser.readMandatorySegment("UNS");
        edifactParser.readMandatorySegment("CNT");
        edifactParser.readMandatorySegment("CNT");
        edifactParser.readMandatorySegment("UNT");
        edifactParser.readMandatorySegment("UNZ");

        return interchange;
    }

}
