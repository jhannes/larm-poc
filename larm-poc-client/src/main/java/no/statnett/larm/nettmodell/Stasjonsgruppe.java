package no.statnett.larm.nettmodell;

public class Stasjonsgruppe {

    private String navn;
    private Elspotområde elspotområde;

    public Stasjonsgruppe(String navn, Elspotområde elspotområde) {
        this.navn = navn;
        this.elspotområde = elspotområde;
    }

}
