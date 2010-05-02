package no.statnett.larm.core.repository;


public interface Specification<T> {

    Class<T> getEntityType();

}
