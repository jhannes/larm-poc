package no.statnett.larm.nettmodell;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Stasjonsgruppe {

	@Id
	@GeneratedValue
	private Integer id;

	private String navn;

	@ManyToOne
	private Elspotområde elspotområde;

	private String beskrivelse;
	
	Stasjonsgruppe() {
	}

	public Stasjonsgruppe(String navn, String beskrivelse, Elspotområde elspotområde) {
		this(navn, elspotområde);
		this.beskrivelse = beskrivelse;
	}

	public Stasjonsgruppe(String navn, Elspotområde elspotområde) {
		this.navn = navn;
		this.elspotområde = elspotområde;
	}

	@Override
	public String toString() {
		return "Stasjonsgruppe<" + navn + "," + elspotområde + ">";
	}
	
	public Integer getId() {
		return id;
	}
	
	public String getNavn() {
		return navn;
	}

	public Elspotområde getElspotområde() {
		return elspotområde;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Stasjonsgruppe)) return false;
		Stasjonsgruppe other = (Stasjonsgruppe)obj;
		
		return nullSafeEquals(getNavn(), other.getNavn()) &&
			nullSafeEquals(id, other.getId());
	}

	private<T> boolean nullSafeEquals(T a, T b) {
		return a != null ? a.equals(b) : (b == null);
	}

	@Override
	public int hashCode() {
		return getNavn().hashCode();
	}
}
