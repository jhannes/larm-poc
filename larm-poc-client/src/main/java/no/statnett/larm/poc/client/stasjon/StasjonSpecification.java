package no.statnett.larm.poc.client.stasjon;

import no.statnett.larm.core.repository.Specification;
import no.statnett.larm.core.repository.hibernate.HibernateSpecification;
import no.statnett.larm.core.repository.inmemory.InmemorySpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

public class StasjonSpecification implements Specification<Stasjon>, HibernateSpecification<Stasjon>, InmemorySpecification<Stasjon> {

    private boolean includeF02;
    private boolean includeF01;

    public boolean getIncludeF02() {
        return includeF02;
    }

    public boolean getIncludeF01() {
        return includeF01;
    }

    public void setIncludeF01(boolean includeF01) {
        this.includeF01 = includeF01;
    }

    public void setIncludeF02(boolean includeF02) {
        this.includeF02 = includeF02;
    }

    public Class<Stasjon> getEntityType() {
        return Stasjon.class;
    }

    public boolean matches(Stasjon entity) {
        return getFastområder().isEmpty() || getFastområder().contains(entity.getFastområde());
    }

    private List<String> getFastområder() {
        ArrayList<String> fastområder = new ArrayList<String>();
        if (includeF01) fastområder.add("F01");
        if (includeF02) fastområder.add("F02");
        return fastområder;
    }

    public DetachedCriteria createCriteria() {
        DetachedCriteria criteria = DetachedCriteria.forClass(getEntityType());
        if (!getFastområder().isEmpty()) criteria.add(Restrictions.in("fastområde", getFastområder()));
        return criteria;
    }


}
