package no.statnett.larm.nettmodell;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Stasjonsgruppe {

    @Id @GeneratedValue
    private Integer id;

    private String navn;

    @ManyToOne
    private Elspotomr�de elspotomr�de;

    Stasjonsgruppe() {
    }

    public Stasjonsgruppe(String navn, Elspotomr�de elspotomr�de) {
        this.navn = navn;
        this.elspotomr�de = elspotomr�de;
    }

    @Override
    public String toString() {
        return "Stasjonsgruppe<" + navn + "," + elspotomr�de + ">";
    }

    public Elspotomr�de getElspotomr�de() {
        return elspotomr�de;
    }

}
