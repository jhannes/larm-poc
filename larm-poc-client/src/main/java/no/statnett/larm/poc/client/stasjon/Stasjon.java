package no.statnett.larm.poc.client.stasjon;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Stasjon {
    @Id @GeneratedValue
    private Integer id;

    private String navn;
    private String fastomr�de;

    public static Stasjon medNavnOgFastomr�de(String navn, String fastomr�de) {
        Stasjon stasjon = new Stasjon();
        stasjon.navn = navn;
        stasjon.fastomr�de = fastomr�de;
        return stasjon;
    }

    public String getFastomr�de() {
        return fastomr�de;
    }

    public String getNavn() {
        return navn;
    }

    @Override
    public String toString() {
        return "Stasjon<" + navn + ",fastomr�de=" + fastomr�de + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stasjon)) return false;

        Stasjon stasjon = (Stasjon) o;
        return nullSafeEquals(navn, stasjon.navn) && nullSafeEquals(fastomr�de, stasjon.fastomr�de);
    }

    private<T> boolean nullSafeEquals(T a, T b) {
        return a != null ? a.equals(b) : (b == null);
    }

    @Override
    public int hashCode() {
        return nullSafeHashCode(navn, fastomr�de);
    }

    private int nullSafeHashCode(Object... fields) {
        int result = 31;
        for (Object field : fields) {
            if (field != null) result ^= field.hashCode();
        }
        return result;
    }
}
