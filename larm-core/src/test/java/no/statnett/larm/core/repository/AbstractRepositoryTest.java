package no.statnett.larm.core.repository;

import org.junit.Test;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public abstract class AbstractRepositoryTest {
    private Repository repository = getRepository();

    abstract Repository getRepository();

    @Test
    public void shouldFindInsertedEntities() {
        ExampleEntity entity1 = new ExampleEntity("A");
        ExampleEntity entity2 = new ExampleEntity("B");

        repository.insert(entity1);
        repository.insert(entity2);

        assertThat(repository.findAll(ExampleEntity.class)).contains(entity1, entity2);
        assertThat(repository.findAll(String.class)).isEmpty();
    }

    @Test
    public void shouldReturnKeyOnInsert() throws Exception {
        ExampleEntity entity1 = new ExampleEntity("A");
        ExampleEntity entity2 = new ExampleEntity("B");

        Serializable key1 = repository.insert(entity1);
        Serializable key2 = repository.insert(entity2);
        assertThat(key1).isNotEqualTo(key2);

        assertThat(repository.retrieve(ExampleEntity.class, key1)).isEqualTo(entity1);
        assertThat(repository.retrieve(ExampleEntity.class, key1)).isNotSameAs(entity1);
        assertThat(repository.retrieve(ExampleEntity.class, key2)).isEqualTo(entity2);
    }

    @Test
    public void shouldSearchBySpecification() {
        ExampleEntity entityWithStatus1 = ExampleEntity.withNameAndStatus("A", 1);
        ExampleEntity entityWithStatus2 = ExampleEntity.withNameAndStatus("B", 2);
        ExampleEntity entityWithStatus3 = ExampleEntity.withNameAndStatus("C", 3);

        repository.insertAll(entityWithStatus1, entityWithStatus2, entityWithStatus3);

        assertThat(repository.find(ExampleEntitySpecification.withStatuses(1,2)))
                .contains(entityWithStatus1, entityWithStatus2)
                .excludes(entityWithStatus3);
    }

    @Test
    public void findShouldClone() {
        ExampleEntity entity = ExampleEntity.withNameAndStatus("A", 1);
        repository.insert(entity);

        List<ExampleEntity> result = repository.find(new ExampleEntitySpecification());
        assertThat(result.get(0)).isEqualTo(entity);
        assertThat(result.get(0)).isNotSameAs(entity);
    }

    @Test(expected = RuntimeException.class)
    public void entitiesMustBeAnnotated() throws Exception {
        repository.insert(new String("test"));
    }

    @Test(expected = RuntimeException.class)
    public void entitiesMustHaveId() throws Exception {
        @Entity
        class EntityWithoutId {

        };

        repository.insert(new EntityWithoutId());
    }

    @Test(expected = RuntimeException.class)
    public void entitiesWithNonGeneratedIdsMustHaveIdSet() throws Exception {
        @Entity
        class EntityWithId {
            @Id
            private Long id;
        };
        repository.insert(new EntityWithId());
    }
}
