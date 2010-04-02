package no.statnett.larm.core.repository.inmemory;

import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.core.repository.Specification;

import javax.persistence.EmbeddedId;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.statnett.larm.core.repository.inmemory.ObjectUtils.cloneEntity;

public class InmemoryRepository implements Repository {
    private Map<EntityKey, Object> store = new HashMap<EntityKey, Object>();
    private static int idSequence = 0;

    @SuppressWarnings("unchecked")
    public <T> List<T> find(Specification<T> specification) {
        InmemorySpecification<T> inmemorySpecification = (InmemorySpecification<T>) specification;
        List<T> result = new ArrayList<T>();
        for (T entity : findAll(specification.getEntityType())) {
            if (inmemorySpecification.matches(entity)) result.add(entity);
        }
        return result;
    }

    public Serializable insert(Object entity) {
        Serializable key = getOrGenerateId(entity);
        store.put(entityKey(key, entity.getClass()), entity);
        return key;
    }

    private EntityKey entityKey(Serializable key, Class<?> entityType) {
        return new EntityKey(key, entityType);
    }

    private Serializable getOrGenerateId(Object entity) {
        Field idField = findIdField(entity);

        if (idField.getAnnotation(GeneratedValue.class) != null) {
            int id = idSequence++;
            try {
                idField.set(entity, id);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(idField + " should be accessible", e);
            }
            return id;
        } else {
            Serializable id = ObjectUtils.cloneSerializable((Serializable) ObjectUtils.fieldValue(idField, entity));
            if (id == null) {
                throw new IllegalArgumentException("Assigned id " + idField + " cannot be null");
            }
            return id;

        }

    }

    private Field findIdField(Object entity) {
        Field idField = ObjectUtils.findFieldWithAnnotation(entity.getClass(), Id.class);
        if (idField == null) {
            idField = ObjectUtils.findFieldWithAnnotation(entity.getClass(), EmbeddedId.class);
        }
        if (idField == null) {
            throw new IllegalArgumentException(entity.getClass() + " must have field with  @Id or @EmbeddedId");
        }
        return idField;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(Class<T> entityClass) {
        ArrayList<T> result = new ArrayList<T>();
        for (Object o : store.values()) {
            if (entityClass.isAssignableFrom(o.getClass())) result.add(cloneEntity((T) o));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> T retrieve(Class<T> entityClass, Serializable key) {
        return ObjectUtils.cloneEntity((T) store.get(entityKey(key, entityClass)));
    }

    public void insertAll(Object... entities) {
        for (Object entity : entities) {
            insert(entity);
        }
    }

}
