package no.statnett.larm.core.repository.hibernate;

import org.hibernate.criterion.DetachedCriteria;

public interface HibernateSpecification<T> {

    DetachedCriteria createCriteria();
    
}
