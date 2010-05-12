package no.statnett.larm.core.repository;

import no.statnett.larm.core.repository.inmemory.InmemoryRepository;

public class InmemoryRepositoryTest extends AbstractRepositoryTest {

    @Override
    protected Repository getRepository() {
        return new InmemoryRepository();
    }

}
