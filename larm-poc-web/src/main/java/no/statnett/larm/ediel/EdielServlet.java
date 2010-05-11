package no.statnett.larm.ediel;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.statnett.larm.LarmHibernateRepository;
import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.edifact.UnaSegment;
import no.statnett.larm.edifact.UnbSegment;
import no.statnett.larm.edifact.UnhSegment;
import no.statnett.larm.edifact.UntSegment;
import no.statnett.larm.edifact.UnzSegment;
import no.statnett.larm.nettmodell.Stasjonsgruppe;
import no.statnett.larm.nettmodell.StasjonsgruppeSpecification;
import no.statnett.larm.reservekraft.ReservekraftBud;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class EdielServlet extends HttpServlet {

    private static final long serialVersionUID = -8962987923221263389L;
    private Repository repository;

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Reader reader = req.getReader();
        read(reader);
        writeAperak(resp.getWriter());
    }

    private void writeAperak(PrintWriter writer) throws IOException {
        new UnaSegment(":+.? '").write(writer);
        new UnbSegment().write(writer);
        new UnhSegment("APERAK", "D", "96A", "UN", "EDIEL2").write(writer);

        AperakMessage aperakMessage = new AperakMessage();
        aperakMessage.setBeginMessage(new BgmSegment());
        aperakMessage.setArrivalTime(DtmSegment.withDateTime(new DateTime()));
        aperakMessage.setMessageDate(DtmSegment.withDateTime(new DateTime()));
        aperakMessage.setReferencedMessage(new RffSegment());
        aperakMessage.write(writer);

        new UntSegment("7", "1").write(writer); // TODO: Must count segments!
        new UnzSegment("1", "29").write(writer);
    }

    void read(Reader reader) throws IOException {
        QuoteParser quoteParser = new QuoteParser(reader);
        QuoteMessage quoteMessage = quoteParser.parseMessage();
        DateTime processingStartTime = quoteMessage.getProcessingStartTime().getDateTime();
        DateTime processingEndTime = quoteMessage.getProcessingEndTime().getDateTime();

        for (LinSegment linSegment : quoteMessage.getLineItems()) {
            repository.insert(lesBud(linSegment, processingStartTime, processingEndTime));
        }
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

    @Override
    public void init() throws ServletException {
        repository = LarmHibernateRepository.withJndiUrl("jdbc/primaryDs");
    }
}
