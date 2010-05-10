package no.statnett.larm.ediel;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.nettmodell.Stasjonsgruppe;
import no.statnett.larm.nettmodell.StasjonsgruppeSpecification;
import no.statnett.larm.reservekraft.ReservekraftBud;

import org.joda.time.DateMidnight;

public class EdielServlet extends HttpServlet {

	private static final long serialVersionUID = -8962987923221263389L;
	private Repository repository;

	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Reader reader = req.getReader();
		QuoteParser quoteParser= new QuoteParser(reader);
		QuoteMessage quoteMessage = quoteParser.parseMessage();
		
		for (LinSegment linSegment : quoteMessage.getLineItems()) {
			repository.insert(lesBud(linSegment));
		}
		
	}

	ReservekraftBud lesBud(LinSegment linSegment) {
		String stasjonsGruppeNavn = linSegment.getLocation().getLocationIdentification();
		List<Stasjonsgruppe> list = repository.find(StasjonsgruppeSpecification.medLocationId(stasjonsGruppeNavn));
		
		ReservekraftBud reserveKraftBud = new ReservekraftBud(list.get(0));
		
		for (PriSegment priSegment : linSegment.getPriceDetails()) {
			reserveKraftBud.setVolumForTidsrom(new DateMidnight().toDateTime(), new DateMidnight().toDateTime(), 360);
		}
		return reserveKraftBud;
	}

}
