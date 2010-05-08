package no.statnett.larm.core.repository.hibernate;

import no.statnett.larm.core.repository.Specification;

import org.hibernate.criterion.DetachedCriteria;

public interface HibernateSpecification<T> extends Specification<T> {

    DetachedCriteria createCriteria();

}
