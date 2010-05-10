package no.statnett.larm.nettmodell;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import no.statnett.larm.core.repository.hibernate.HibernateSpecification;
import no.statnett.larm.core.repository.inmemory.InmemorySpecification;


public class StasjonsgruppeSpecification implements InmemorySpecification<Stasjonsgruppe>, HibernateSpecification<Stasjonsgruppe> {

	private String navn;

	public static StasjonsgruppeSpecification medLocationId(String stasjonsGruppeNavn) {
		StasjonsgruppeSpecification specification = new StasjonsgruppeSpecification();
		specification.navn = stasjonsGruppeNavn;
		return specification;
	}

	@Override
	public Class<Stasjonsgruppe> getEntityType() {
		return Stasjonsgruppe.class;
	}

	@Override
	public boolean matches(Stasjonsgruppe entity) {
		return navn.equals(entity.getNavn());
	}

	@Override
	public DetachedCriteria createCriteria() {
		return DetachedCriteria.forClass(getEntityType()).add(Restrictions.eq("navn", navn));
	}

}
