package no.statnett.larm.ediel;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.edifact.EdifactInterchange;
import no.statnett.larm.nettmodell.Stasjonsgruppe;
import no.statnett.larm.nettmodell.StasjonsgruppeSpecification;
import no.statnett.larm.reservekraft.ReservekraftBud;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class EdielService {

    private final Repository repository;
    @SuppressWarnings("unused")
    private String fileName;

    public EdielService(Repository repository) {
        this.repository = repository;
    }

    public void process(String fileName, Reader edifactRequest, Appendable edifactResponse) throws IOException {
        this.fileName = fileName;
        QuoteParser quoteParser = new QuoteParser(edifactRequest);
        QuoteMessage quoteMessage = quoteParser.parseMessage();
        AperakMessage response = readMessage(quoteMessage);
        writeMessage(edifactResponse, response);
    }

    private void writeMessage(Appendable writer, AperakMessage aperakMessage) throws IOException {
        EdifactInterchange interchange = new EdifactInterchange(aperakMessage);
        interchange.setSyntax("UNOB", "2");
        interchange.setSender(aperakMessage.getMessageFrom().getPartyId(), "14", "REGULERKRAFT");
        interchange.setRecipient(aperakMessage.getDocumentRecipient().getPartyId(), "14", "REGULERKRAFT");
        interchange.setControlReference(String.valueOf(System.currentTimeMillis()));
        interchange.write(writer);
    }

    AperakMessage createResponse(String reference, String senderPartyId) {
        AperakMessage aperakMessage = new AperakMessage();
        aperakMessage.setBeginMessage(new BgmSegment().setMessageFunction("29"));
        aperakMessage.setArrivalTime(DtmSegment.withDateTime(new DateTime()));
        aperakMessage.setMessageDate(DtmSegment.withDateTime(new DateTime()));
        aperakMessage.setReferencedMessage(new RffSegment().setReference(reference));

        NadSegment messageFrom = new NadSegment("7080000923168", "9");
        messageFrom.setCity("Oslo");
        messageFrom.setCountry("NO");
        messageFrom.setContactInfo(new CtaSegment("MR").setDepartment("Landsentralen"));
        aperakMessage.setMessageFrom(messageFrom);

        aperakMessage.setDocumentRecipient(new NadSegment(senderPartyId, "9"));
        return aperakMessage;
    }

    AperakMessage readMessage(QuoteMessage quoteMessage) throws IOException {
        DateTime processingStartTime = quoteMessage.getProcessingStartTime().getDateTime();
        DateTime processingEndTime = quoteMessage.getProcessingEndTime().getDateTime();

        for (LinSegment linSegment : quoteMessage.getLineItems()) {
            repository.insert(lesBud(linSegment, processingStartTime, processingEndTime));
        }

        return createResponse("", "");
    }

    ReservekraftBud lesBud(LinSegment linSegment, DateTime processingStartTime, DateTime processingEndTime) {
        String stasjonsGruppeNavn = linSegment.getLocation().getLocationIdentification();
        ReservekraftBud reserveKraftBud = new ReservekraftBud(findStasjonsgruppeByNavn(stasjonsGruppeNavn));
        reserveKraftBud.setBudreferanse(linSegment.getPriceQuote().getReference());
        if (linSegment.getDuration() != null) reserveKraftBud.setVarighet(linSegment.getDuration().getQuantity());
        if (linSegment.getRestingTime() != null) reserveKraftBud.setHviletid(linSegment.getRestingTime().getQuantity());
        reserveKraftBud.setBudperiode(new Interval(processingStartTime, processingEndTime));

        for (PriSegment priSegment : linSegment.getPriceDetails()) {
            reserveKraftBud.setVolumForTidsrom(priSegment.getProcessingTime().getPeriod(), priSegment.getVolume());
        }
        return reserveKraftBud;
    }

    private Stasjonsgruppe findStasjonsgruppeByNavn(String stasjonsGruppeNavn) {
        List<Stasjonsgruppe> list = repository.find(StasjonsgruppeSpecification.medLocationId(stasjonsGruppeNavn));
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Fant ingen stasjonsgruppe med navn " + stasjonsGruppeNavn);
        }
        return list.get(0);
    }
}
