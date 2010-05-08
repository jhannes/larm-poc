package no.statnett.larm.nettmodell;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Elspotområde {

    @Id @GeneratedValue
    private Integer id;

    private String navn;

    Elspotområde() {
    }

    public Elspotområde(String navn) {
        this.navn = navn;
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "Elspotområde<" + navn + ">";
    }

}
