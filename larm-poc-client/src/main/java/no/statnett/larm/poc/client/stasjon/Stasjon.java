package no.statnett.larm.poc.client.stasjon;

public class Stasjon {
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
}
