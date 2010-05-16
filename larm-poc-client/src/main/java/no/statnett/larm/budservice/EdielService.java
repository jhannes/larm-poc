package no.statnett.larm.budservice;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.ediel.AperakMessage;
import no.statnett.larm.ediel.BgmSegment;
import no.statnett.larm.ediel.CtaSegment;
import no.statnett.larm.ediel.DtmSegment;
import no.statnett.larm.ediel.LinSegment;
import no.statnett.larm.ediel.NadSegment;
import no.statnett.larm.ediel.PriSegment;
import no.statnett.larm.ediel.QuoteMessage;
import no.statnett.larm.ediel.QuoteParser;
import no.statnett.larm.ediel.RffSegment;
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
    private QuoteParser quoteParser = new QuoteParser();

    public EdielService(Repository repository) {
        this.repository = repository;
    }

    public void process(String fileName, Reader edifactReader, Appendable edifactWriter) throws IOException {
        this.fileName = fileName;
        EdifactInterchange interchange = quoteParser.parseInterchange(edifactReader);
        EdifactInterchange response = readInterchange(interchange);
        response.writeTo(edifactWriter);
    }

    EdifactInterchange readInterchange(EdifactInterchange interchange) {
        QuoteMessage quoteMessage = (QuoteMessage)interchange.getMessage();

        DateTime processingStartTime = quoteMessage.getProcessingStartTime().getDateTime();
        DateTime processingEndTime = quoteMessage.getProcessingEndTime().getDateTime();

        for (LinSegment linSegment : quoteMessage.getLineItems()) {
            repository.insert(lesBud(linSegment, processingStartTime, processingEndTime));
        }

        String senderPartyId = quoteMessage.getMessageFrom().getPartyId();
        return createResponse(interchange.getControlReference(), senderPartyId);
    }

    EdifactInterchange createResponse(String reference, String senderPartyId) {
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

        EdifactInterchange response = new EdifactInterchange(aperakMessage);
        response.setSyntax("UNOB", "2");
        response.setSender(aperakMessage.getMessageFrom().getPartyId(), "14", "REGULERKRAFT");
        response.setRecipient(aperakMessage.getDocumentRecipient().getPartyId(), "14", "REGULERKRAFT");
        response.setControlReference(String.valueOf(System.currentTimeMillis()));
        return response;
    }

    ReservekraftBud lesBud(LinSegment linSegment, DateTime processingStartTime, DateTime processingEndTime) {
        String stasjonsGruppeNavn = linSegment.getLocation().getLocationIdentification();
        ReservekraftBud reserveKraftBud = new ReservekraftBud(findStasjonsgruppeByNavn(stasjonsGruppeNavn));
        reserveKraftBud.setBudreferanse(linSegment.getPriceQuote().getReference());
        if (linSegment.getAvailability() != null) reserveKraftBud.setVarighet(linSegment.getAvailability().getQuantity());
        if (linSegment.getRestingTime() != null) reserveKraftBud.setHviletid(linSegment.getRestingTime().getQuantity());
        reserveKraftBud.setBudperiode(new Interval(processingStartTime, processingEndTime));

        Integer pris = 0;
        for (PriSegment priSegment : linSegment.getPriceDetails()) {
            reserveKraftBud.setVolumForTidsrom(priSegment.getProcessingTime().getPeriod(), priSegment.getVolume());
            pris = Integer.valueOf(priSegment.getPrice());
        }
        reserveKraftBud.setPris(pris);
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
