package no.statnett.larm.ediel;

import java.io.IOException;

import no.statnett.larm.edifact.QualifiedEdifactSegmentGroup;
import no.statnett.larm.edifact.Segment;
import no.statnett.larm.edifact.SegmentSource;

/**
 * Name And Address
 */
@Segment("NAD")
public class NadSegment extends QualifiedEdifactSegmentGroup {

    private CtaSegment contactInfo;

    public String getPartyId() {
        return getElementComponent(1, 0);
    }

    @Override
    public String getQualifier() {
        return getElementData(0);
    }

    public void readSegmentGroup(SegmentSource edifactParser) throws IOException {
        contactInfo = edifactParser.readOptionalSegment(CtaSegment.class);
        edifactParser.readOptionalSegment(ComSegment.class);
    }

    public String getCity() {
        return getElementData(5);
    }

    public String getCountry() {
        return getElementData(8);
    }

    public CtaSegment getContactInfo() {
        return contactInfo;
    }

}
