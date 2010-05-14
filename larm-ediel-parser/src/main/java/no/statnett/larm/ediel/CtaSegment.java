package no.statnett.larm.ediel;

import no.statnett.larm.edifact.EdifactSegment;
import no.statnett.larm.edifact.Segment;

@Segment("CTA")
public class CtaSegment extends EdifactSegment {

    public CtaSegment() {
    }

    public CtaSegment(String function) {
        setFunction(function);
    }

    public void setFunction(String function) {
        setElementComponent(0, 0, function);
    }

    public String getDepartment() {
        return getElementComponent(1, 2);
    }

    public CtaSegment setDepartment(String department) {
        setElementComponent(1, 2, department);
        return this;
    }

}
