package no.statnett.larm.core.repository;

import org.hibernate.criterion.DetachedCriteria;

public interface Specification<T> {

    Class<T> getEntityType();

}
