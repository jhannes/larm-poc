package no.statnett.larm.ediel;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;

import org.junit.Test;

public class QuoteParserTest {

    private String exampleFile =
        "UNA:+.? '" +
        "UNB+UNOC:3+7080005050999:14:REGULERMARKED+7080000923168:14:REGULERMARKED+091130:1434+statkraft-987++++1'" +
        "UNH+1+QUOTES:D:96A:UN:EDIEL2+S'" +
        "BGM+310+2009113000025305+9+AB'" +
        "DTM+137:200911301554:203'" +
        "DTM+163:200912010000:203'" +
        "DTM+164:200912012400:203'" +
        "DTM+ZZZ:1:805'" +
        "CUX+2:NOK'" +
        "NAD+FR+7080005050999::9'" +
        "NAD+DO+7080000923168::9+++++++NO'" +
        "LIN+1++1608:::SM'" +
        "DTM+44:120:806'" +
        "DTM+66:180:806'" +
        "PRI+CAL:450'RNG+4+Z01:-39'DTM+324:200912010000200912010100:Z13'" +
        "PRI+CAL:450'RNG+4+Z01:-39'DTM+324:200912010100200912010200:Z13'" +
        "RFF+PR:2009492-3-1'" +
        "LOC+90+NOKG00049::SM'" +
        "UNS+S'" +
        "CNT+1:-410'" +
        "CNT+ZZZ:14400'" +
        "UNT+76+1'" +
        "UNZ+1+statkraft-987'";

    @Test
    public void shouldParseQuotes() throws Exception {
        QuoteParser quoteParser = new QuoteParser(new StringReader(exampleFile));

        QuoteMessage message = quoteParser.parseMessage();
        assertThat(message.getBeginMessage().getMessageTime()).isEqualTo("2009113000025305");
        assertThat(message.getMessageDate().getTime()).isEqualTo("200911301554");
        assertThat(message.getProcessingStartTime().getTime()).isEqualTo("200912010000");
        assertThat(message.getProcessingEndTime().getTime()).isEqualTo("200912012400");
        assertThat(message.getOffsetToUTC().getHour()).isEqualTo("1");
        assertThat(message.getCurrency().getCurrencyCode()).isEqualTo("NOK");
        assertThat(message.getMessageFrom().getPartyId()).isEqualTo("7080005050999");
        assertThat(message.getDocumentRecipient().getPartyId()).isEqualTo("7080000923168");

        LinSegment lineItem = message.getLineItems().get(0);
        assertThat(lineItem.getItemNumber()).isEqualTo("1608");
        assertThat(lineItem.getResponsibleAgency()).isEqualTo("SM");
        assertThat(lineItem.getAvailability().getMinutes()).isEqualTo("120");
        assertThat(lineItem.getRestingTime().getMinutes()).isEqualTo("180");

        PriSegment priceDetails = lineItem.getPriceDetails().get(0);
        assertThat(priceDetails.getPrice()).isEqualTo("450");
        assertThat(priceDetails.getRange().getUnit()).isEqualTo("Z01");
        assertThat(priceDetails.getRange().getMinimum()).isEqualTo("-39");
        assertThat(priceDetails.getProcessingTime().getRange()).isEqualTo("200912010000200912010100");

        priceDetails = lineItem.getPriceDetails().get(1);
        assertThat(priceDetails.getProcessingTime().getRange()).isEqualTo("200912010100200912010200");
        assertThat(lineItem.getPriceQuote().getReference()).isEqualTo("2009492-3-1");
        assertThat(lineItem.getLocation().getNetArea()).isEqualTo("NOKG00049");
    }

    @Test
    public void shouldParseAllTestQuotesFiles() throws Exception {
        File quotesFileTestDir = new File("src/test/ediel/quotes");
        for (File quotesFile : quotesFileTestDir.listFiles()) {
            QuoteParser quoteParser = new QuoteParser(new FileReader(quotesFile));
            QuoteMessage message = quoteParser.parseMessage();
            assertThat(message).isNotNull();
        }
    }

}
