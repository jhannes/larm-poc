package no.statnett.larm.nettmodell;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Elspotomr�de {

    @Id @GeneratedValue
    private Integer id;

    private String navn;

    Elspotomr�de() {
    }

    public Elspotomr�de(String navn) {
        this.navn = navn;
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "Elspotomr�de<" + navn + ">";
    }

}
