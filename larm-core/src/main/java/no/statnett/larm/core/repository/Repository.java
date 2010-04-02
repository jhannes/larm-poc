package no.statnett.larm.core.repository;

import java.util.List;

public interface Repository {
    <T> List<T> find(Specification<T> stasjonSpecification);
}
