package no.statnett.larm.reservekraft;

import static no.statnett.larm.core.repository.inmemory.ObjectMatching.blankOrContains;

import java.util.Collection;

import no.statnett.larm.core.repository.hibernate.HibernateSpecification;
import no.statnett.larm.core.repository.inmemory.InmemorySpecification;
import no.statnett.larm.nettmodell.Elspotomr�de;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateMidnight;
import org.joda.time.Interval;

public class ReservekraftBudSpecification implements HibernateSpecification<ReservekraftBud>, InmemorySpecification<ReservekraftBud> {

    private Collection<Elspotomr�de> elspotomr�der;
    private DateMidnight driftsd�gn;
    private Interval driftsperiode;

    public static ReservekraftBudSpecification forOmr�der(Collection<Elspotomr�de> elspotomr�der) {
        ReservekraftBudSpecification specification = new ReservekraftBudSpecification();
        specification.elspotomr�der = elspotomr�der;
        return specification;
    }

    public Class<ReservekraftBud> getEntityType() {
        return ReservekraftBud.class;
    }

    public void setDriftsd�gn(DateMidnight driftsd�gn) {
        this.driftsd�gn = driftsd�gn;
    }

    public void setDriftsperiode(Interval driftsperiode) {
        this.driftsperiode = driftsperiode;
    }

    @Override
    public DetachedCriteria createCriteria() {
        DetachedCriteria criteria = DetachedCriteria.forClass(getEntityType());
        criteria.createAlias("stasjonsgruppe", "stasjonsgruppe");
        addInUnlessBlank(criteria, "stasjonsgruppe.elspotomr�de", elspotomr�der);

        if (driftsd�gn != null) {
            criteria.add(Restrictions.le("startTid", driftsd�gn.plusDays(1).toDateTime()));
            criteria.add(Restrictions.ge("sluttTid", driftsd�gn.toDateTime()));
        }
        if (driftsperiode != null) {
            criteria.createAlias("volumPerioder", "volumperiode");
            criteria.add(Restrictions.le("volumperiode.startTid", driftsperiode.getEnd()));
            criteria.add(Restrictions.ge("volumperiode.sluttTid", driftsperiode.getStart()));
            criteria.add(Restrictions.gt("volumperiode.tilgjengeligVolum", 0));
        }

        return criteria;
    }

    private<T> void addInUnlessBlank(DetachedCriteria criteria, String propertyName, Collection<T> values) {
        if (values == null || values.isEmpty()) return;
        criteria.add(Restrictions.in(propertyName, values));
    }

    @Override
    public boolean matches(ReservekraftBud entity) {
        return blankOrContains(elspotomr�der, entity.getElspotomr�de()) &&
            inneholderDriftsd�gn(driftsd�gn, entity.getBudperiode()) &&
            harVolumIPeriode(driftsperiode, entity.getVolumPerioder());
    }

    private boolean harVolumIPeriode(Interval interval, Collection<Volumperiode> volumPerioder) {
        if (interval == null) return true;
        for (Volumperiode volumperiode : volumPerioder) {
            if (interval.overlaps(volumperiode.getPeriode()) && volumperiode.getTilgjengeligVolum() > 0) {
                return true;
            }
        }
        return false;
    }

    private boolean inneholderDriftsd�gn(DateMidnight aktueltD�gn, Interval budperiode) {
        if (aktueltD�gn == null) return true;
        return aktueltD�gn.toInterval().overlaps(budperiode);
    }

}
