package no.statnett.larm.ediel;

import no.statnett.larm.edifact.EdifactSegment;
import no.statnett.larm.edifact.Segment;

@Segment("BGM")
public class BgmSegment extends EdifactSegment {

	public String getMessageTime() {
		return getElementData(1);
	}


}
