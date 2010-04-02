package no.statnett.larm.poc.client.stasjon;

public class Stasjon {
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
}
