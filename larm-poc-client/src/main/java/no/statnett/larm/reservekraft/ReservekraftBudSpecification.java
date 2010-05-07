package no.statnett.larm.reservekraft;

import java.util.Collection;

import no.statnett.larm.core.repository.Specification;
import no.statnett.larm.nettmodell.Elspotområde;

import org.joda.time.DateMidnight;
import org.joda.time.Interval;

public class ReservekraftBudSpecification implements Specification<ReservekraftBud> {

    private Collection<Elspotområde> elspotområder;
    private DateMidnight driftsdøgn;
    private Interval driftsperiode;

    public static ReservekraftBudSpecification forOmråder(Collection<Elspotområde> elspotområder) {
        ReservekraftBudSpecification specification = new ReservekraftBudSpecification();
        specification.elspotområder = elspotområder;
        return specification;
    }

    @Override
    public Class<ReservekraftBud> getEntityType() {
        return ReservekraftBud.class;
    }

    public ReservekraftBudSpecification setDriftsdøgn(DateMidnight driftsdøgn) {
        this.driftsdøgn = driftsdøgn;
        return this;
    }

    public void setDriftsperiode(Interval driftsperiode) {
        this.driftsperiode = driftsperiode;
    }

}
