package no.statnett.larm.ediel;

import no.statnett.larm.edifact.EdifactSegment;
import no.statnett.larm.edifact.Segment;

@Segment("CTA")
public class CtaSegment extends EdifactSegment {

    public String getDepartment() {
        return getElementComponent(1, 2);
    }

}
