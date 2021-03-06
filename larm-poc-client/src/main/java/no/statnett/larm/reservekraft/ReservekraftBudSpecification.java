package no.statnett.larm.reservekraft;

import static no.statnett.larm.core.repository.inmemory.ObjectMatching.blankOrContains;
import static org.hibernate.criterion.Restrictions.gt;
import static org.hibernate.criterion.Restrictions.lt;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import no.statnett.larm.core.repository.hibernate.HibernateSpecification;
import no.statnett.larm.core.repository.inmemory.InmemorySpecification;
import no.statnett.larm.core.repository.inmemory.PostProcessSpecification;
import no.statnett.larm.nettmodell.Elspotområde;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.DistinctRootEntityResultTransformer;
import org.joda.time.DateMidnight;
import org.joda.time.Interval;

public class ReservekraftBudSpecification implements HibernateSpecification<ReservekraftBud>,
        InmemorySpecification<ReservekraftBud>, PostProcessSpecification<ReservekraftBud>, Serializable {

    private static final long serialVersionUID = -1578585305673220372L;
    private Collection<Elspotområde> elspotområder;
    private DateMidnight driftsdøgn;
    private Interval driftsperiode;

    public static ReservekraftBudSpecification forOmråder(
            Collection<Elspotområde> elspotområder) {
        ReservekraftBudSpecification specification = new ReservekraftBudSpecification();
        specification.elspotområder = elspotområder;
        return specification;
    }

    public Class<ReservekraftBud> getEntityType() {
        return ReservekraftBud.class;
    }

    public DateMidnight getDriftsdøgn() {
        return driftsdøgn;
    }

    public void setDriftsdøgn(DateMidnight driftsdøgn) {
        this.driftsdøgn = driftsdøgn;
    }

    public void setDriftsperiode(Interval driftsperiode) {
        this.driftsperiode = driftsperiode;
    }

    public Collection<Elspotområde> getElspotområder() {
        return elspotområder;
    }

    @Override
    public DetachedCriteria createCriteria() {
        DetachedCriteria criteria = DetachedCriteria.forClass(getEntityType());
        criteria.createAlias("stasjonsgruppe", "stasjonsgruppe");
        addInUnlessBlank(criteria, "stasjonsgruppe.elspotområde", elspotområder);

        if (driftsdøgn != null) {
            criteria.add(Restrictions.le("startTid", driftsdøgn.plusDays(1).toDateTime()));
            criteria.add(Restrictions.ge("sluttTid", driftsdøgn.toDateTime()));
        }
        if (driftsperiode != null) {
            criteria.createCriteria("volumPerioder", "volumperiode")
                    .add(lt("volumperiode.startTid", driftsperiode.getEnd()))
                    .add(gt("volumperiode.sluttTid", driftsperiode.getStart()))
                    .add(gt("volumperiode.tilgjengeligVolum", 0));
        }
        criteria.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE);

        criteria.addOrder(Order.desc("retning"));
        criteria.addOrder(Order.desc("pris"));

        return criteria;
    }

    private <T> void addInUnlessBlank(DetachedCriteria criteria,
            String propertyName, Collection<T> values) {
        if (values == null || values.isEmpty())
            return;
        criteria.add(Restrictions.in(propertyName, values));
    }

    @Override
    public boolean matches(ReservekraftBud entity) {
        return blankOrContains(elspotområder, entity.getElspotområde())
                && inneholderDriftsdøgn(driftsdøgn, entity.getBudperiode())
                && harVolumIPeriode(driftsperiode, entity.getVolumPerioder());
    }

    @Override
    public void postProcess(List<ReservekraftBud> list) {
        Collections.sort(list);
    }

    private boolean harVolumIPeriode(Interval interval,
            Collection<Volumperiode> volumPerioder) {
        if (interval == null)
            return true;
        for (Volumperiode volumperiode : volumPerioder) {
            if (interval.overlaps(volumperiode.getPeriode())
                    && volumperiode.getTilgjengeligVolum() > 0) {
                return true;
            }
        }
        return false;
    }

    private boolean inneholderDriftsdøgn(DateMidnight aktueltDøgn,
            Interval budperiode) {
        if (aktueltDøgn == null)
            return true;
        return aktueltDøgn.toInterval().overlaps(budperiode);
    }

}
