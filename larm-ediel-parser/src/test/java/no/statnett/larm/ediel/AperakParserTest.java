package no.statnett.larm.ediel;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;

import org.joda.time.DateTime;
import org.junit.Test;

public class AperakParserTest {
    private String exampleFile =
        "UNA:+.? '" +
        "UNB+UNOB:2+7080000923168:14:REGULERMARKED+7080005050838:14:REGULERMARKED+100308:1313+1204748++++1'" +
        "UNH+1+APERAK:D:96A:UN:EDIEL2'" +
        "BGM+++27'" +
        "DTM+137:200911301555:203'" +
        "DTM+178:200911301555:203'" +
        "RFF+ACW:2009113000025305'" +
        "NAD+FR+7080000923168::9++++Oslo+++NO'" +
        "CTA+MS+:Landsentralen'" +
        "NAD+DO+7080005050838::9'" +
        "ERC+::999'" +
        "FTX+AA=++999::260+Pris må være i hele 5 kr'" +
        "UNT+11+1'" +
        "UNZ+1+1204748'";

    @Test
    public void shouldParseAperak() throws Exception {
        AperakParser aperakParser = new AperakParser(new StringReader(exampleFile));

        AperakMessage message = aperakParser.parseMessage();
        assertThat(message.getBeginMessage().getMessageFunction()).isEqualTo("27");
        assertThat(message.getReferencedMessage().getReference()).isEqualTo("2009113000025305");
        assertThat(message.getMessageDate().getDateTime()).isEqualTo(new DateTime(2009, 11, 30, 15, 55, 0, 0));
        assertThat(message.getArrivalTime().getDateTime()).isEqualTo(new DateTime(2009, 11, 30, 15, 55, 0, 0));

        assertThat(message.getErrorCodes()).hasSize(1);
        assertThat(message.getErrorCodes().get(0).getApplicationError()).isEqualTo("999");
        assertThat(message.getErrorCodes().get(0).getFreeText().getText()).isEqualTo("Pris må være i hele 5 kr");
    }

    @Test
    public void shouldParseAllTestAperakFiles() throws Exception {
        File quotesFileTestDir = new File("src/test/ediel/aperak");
        assertThat(quotesFileTestDir.listFiles()).isNotEmpty();
        for (File quotesFile : quotesFileTestDir.listFiles()) {
            AperakParser quoteParser = new AperakParser(new FileReader(quotesFile));
            AperakMessage message = quoteParser.parseMessage();
            assertThat(message).isNotNull();
        }
    }
}
