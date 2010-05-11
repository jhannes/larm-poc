package no.statnett.larm.ediel;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.InputStreamReader;

import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.core.repository.RepositoryCallback;
import no.statnett.larm.nettmodell.Elspotområde;
import no.statnett.larm.nettmodell.Stasjonsgruppe;
import no.statnett.larm.poc.web.WebTest;
import no.statnett.larm.reservekraft.ReservekraftBud;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;

public class EdielUploadWebTest extends WebTest {

    @Test
    public void shouldUploadQuotesFile() throws Exception {
        getRepository().deleteAll(ReservekraftBud.class);
        Elspotområde elspotområde = new Elspotområde("NO1");
        getRepository().insert(elspotområde);
        getRepository().insert(new Stasjonsgruppe("NOKG00116", elspotområde));

        HttpClient client = new HttpClient();

        String nordkraftOrgnr = "7080005050838";
        PostMethod request = new PostMethod(getApplicationUrl() + "ediel/" + nordkraftOrgnr + "/");
        String mimeType = "Application/EDIFACT"; // According to RFC1767 and "NORSK EDIEL BRUKERVEILEDNING bruk av SMTP"
        File quotesFile = new File("src/test/ediel/quotes/QuoteNordkraft.edi");
        request.setRequestEntity(new FileRequestEntity(quotesFile, mimeType));

        int responseCode = client.executeMethod(request);
        assertThat(responseCode).isEqualTo(200);

        AperakParser parser = new AperakParser(new InputStreamReader(request.getResponseBodyAsStream(), request.getResponseCharSet()));
        AperakMessage aperakMessage = parser.parseMessage();
        assertThat(aperakMessage.getErrorCodes()).isEmpty();

        getRepository().execute(new RepositoryCallback() {
            @Override
            public void doInSession(Repository repository) {
                assertThat(repository.findAll(ReservekraftBud.class)).hasSize(2);
                ReservekraftBud reservekraftBud = repository.findAll(ReservekraftBud.class).get(0);
                assertThat(reservekraftBud.getStasjonsgruppe().getNavn()).isEqualTo("NOKG00116");
                assertThat(reservekraftBud.getBudreferanse()).isEqualTo("2009492-3-1");
            }
        });
    }

}
