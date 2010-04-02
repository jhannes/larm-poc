package no.statnett.larm.poc.client.stasjon;

import no.statnett.larm.core.repository.Specification;

public class StasjonSpecification extends Specification<Stasjon> {
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
}
