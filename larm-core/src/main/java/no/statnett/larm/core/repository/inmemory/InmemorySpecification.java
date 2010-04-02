package no.statnett.larm.core.repository.inmemory;

public interface InmemorySpecification<T> {

    boolean matches(T entity);

}
