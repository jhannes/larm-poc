package no.statnett.larm.core.repository.inmemory;

import no.statnett.larm.core.repository.Specification;

public interface InmemorySpecification<T> extends Specification<T> {

    boolean matches(T entity);

}
