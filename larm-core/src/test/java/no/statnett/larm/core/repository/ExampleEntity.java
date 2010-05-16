package no.statnett.larm.core.repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ExampleEntity {

    @SuppressWarnings("unused")
    @Id @GeneratedValue
    private Integer id;
    private String name;
    private int status;

    protected ExampleEntity() {

    }

    public ExampleEntity(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ExampleEntity<" + name + ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ExampleEntity)) return false;
        return nullSafeEquals(name, ((ExampleEntity)obj).name);
    }

    private boolean nullSafeEquals(String a, String b) {
        return a != null ? a.equals(b) : b == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : -1;
    }

    public static ExampleEntity withNameAndStatus(String name, int status) {
        ExampleEntity testEntity = new ExampleEntity(name);
        testEntity.status = status;
        return testEntity;
    }

    public Integer getStatus() {
        return status;
    }
}
