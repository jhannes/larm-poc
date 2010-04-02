package no.statnett.larm.poc.client.stasjon;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Stasjon {
    @Id @GeneratedValue
    private Integer id;

    private String navn;
    private String fastområde;

    public static Stasjon medNavnOgFastområde(String navn, String fastområde) {
        Stasjon stasjon = new Stasjon();
        stasjon.navn = navn;
        stasjon.fastområde = fastområde;
        return stasjon;
    }

    public String getFastområde() {
        return fastområde;
    }

    public String getNavn() {
        return navn;
    }

    @Override
    public String toString() {
        return "Stasjon<" + navn + ",fastområde=" + fastområde + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stasjon)) return false;

        Stasjon stasjon = (Stasjon) o;
        return nullSafeEquals(navn, stasjon.navn) && nullSafeEquals(fastområde, stasjon.fastområde);
    }

    private<T> boolean nullSafeEquals(T a, T b) {
        return a != null ? a.equals(b) : (b == null);
    }

    @Override
    public int hashCode() {
        return nullSafeHashCode(navn, fastområde);
    }

    private int nullSafeHashCode(Object... fields) {
        int result = 31;
        for (Object field : fields) {
            if (field != null) result ^= field.hashCode();
        }
        return result;
    }
}
