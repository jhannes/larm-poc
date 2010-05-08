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
    private Elspotområde elspotområde;

    Stasjonsgruppe() {
    }

    public Stasjonsgruppe(String navn, Elspotområde elspotområde) {
        this.navn = navn;
        this.elspotområde = elspotområde;
    }

    @Override
    public String toString() {
        return "Stasjonsgruppe<" + navn + "," + elspotområde + ">";
    }

    public Elspotområde getElspotområde() {
        return elspotområde;
    }

}
