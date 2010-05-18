package no.statnett.larm.core.repository.inmemory;

import java.util.List;

public interface PostProcessSpecification<T> {

    void postProcess(List<T> list);

}
