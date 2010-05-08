package no.statnett.larm.core.repository.inmemory;

import java.util.Collection;

public class ObjectMatching {

    public static <T> boolean blankOrContains(Collection<T> allowedValues, T value) {
        return allowedValues == null || allowedValues.isEmpty() || allowedValues.contains(value);
    }

}
