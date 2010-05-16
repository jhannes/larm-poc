package no.statnett.larm.ediel;

import no.statnett.larm.edifact.EdifactSegment;
import no.statnett.larm.edifact.Segment;

/** Range */
@Segment("RNG")
public class RngSegment extends EdifactSegment {

    public String getUnit() {
        return getElementComponent(1, 0);
    }

    public String getMinimum() {
        return getElementComponent(1, 1);
    }

    public Integer getQuantity() {
        return Integer.valueOf(getMinimum());
    }

    public void setQuantity(Long data) {
        setElementComponent(1, 1, data.toString());
    }

    public void setUnit(String unit) {
        setElementComponent(1, 0, unit);
    }

    public void setTypeQualifier(String data) {
        setElementComponent(0, 0, data);
    }

}
