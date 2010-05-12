package no.statnett.larm.core.repository;

import no.statnett.larm.core.repository.hibernate.HibernateSpecification;
import no.statnett.larm.core.repository.inmemory.InmemorySpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.Arrays;
import java.util.List;

public class ExampleEntitySpecification implements Specification<ExampleEntity>, InmemorySpecification<ExampleEntity>, HibernateSpecification<ExampleEntity> {
    private List<Integer> statusCodes;


    public Class<ExampleEntity> getEntityType() {
        return ExampleEntity.class;
    }

    public static ExampleEntitySpecification withStatuses(Integer... statusCodes) {
        ExampleEntitySpecification specification = new ExampleEntitySpecification();
        specification.statusCodes = Arrays.asList(statusCodes);
        return specification;
    }

    public boolean matches(ExampleEntity entity) {
        return statusCodes == null || statusCodes.contains(entity.getStatus());
    }


    public DetachedCriteria createCriteria() {
        DetachedCriteria criteria = DetachedCriteria.forClass(getEntityType());
        if (statusCodes != null) criteria.add(Restrictions.in("status", statusCodes));
        return criteria;
    }
}
