package no.statnett.larm.reservekraft;

import java.util.Collection;

import no.statnett.larm.core.repository.Specification;
import no.statnett.larm.nettmodell.Elspotomr�de;

import org.joda.time.DateMidnight;
import org.joda.time.Interval;

public class ReservekraftBudSpecification implements Specification<ReservekraftBud> {

    private Collection<Elspotomr�de> elspotomr�der;
    private DateMidnight driftsd�gn;
    private Interval driftsperiode;

    public static ReservekraftBudSpecification forOmr�der(Collection<Elspotomr�de> elspotomr�der) {
        ReservekraftBudSpecification specification = new ReservekraftBudSpecification();
        specification.elspotomr�der = elspotomr�der;
        return specification;
    }

    @Override
    public Class<ReservekraftBud> getEntityType() {
        return ReservekraftBud.class;
    }

    public ReservekraftBudSpecification setDriftsd�gn(DateMidnight driftsd�gn) {
        this.driftsd�gn = driftsd�gn;
        return this;
    }

    public void setDriftsperiode(Interval driftsperiode) {
        this.driftsperiode = driftsperiode;
    }

}
