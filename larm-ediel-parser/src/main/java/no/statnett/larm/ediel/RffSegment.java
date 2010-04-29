package no.statnett.larm.ediel;

import no.statnett.larm.edifact.QualifiedEdifactSegment;
import no.statnett.larm.edifact.Segment;

@Segment("RFF")
public class RffSegment extends QualifiedEdifactSegment {

	@Override
	public String getQualifier() {
		return getElementComponent(0, 0);
	}

	public String getReference() {
		return getElementComponent(0, 1);
	}

}
