package no.statnett.larm.ediel;

import java.io.IOException;

import no.statnett.larm.edifact.EdifactSegmentWriter;
import no.statnett.larm.edifact.QualifiedEdifactSegmentGroup;
import no.statnett.larm.edifact.Segment;
import no.statnett.larm.edifact.SegmentSource;

/**
 * Name And Address
 */
@Segment("NAD")
public class NadSegment extends QualifiedEdifactSegmentGroup {

    private CtaSegment contactInfo;

    NadSegment() {
    }

    public NadSegment(String partyId, String agency) {
        setPartyId(partyId);
        setResponsibleAgency(agency);
    }

    public String getPartyId() {
        return getElementComponent(1, 0);
    }

    public NadSegment setPartyId(String partyId) {
        setElementComponent(1, 0, partyId);
        return this;
    }

    @Override
    public String getQualifier() {
        return getElementData(0);
    }

    public void setQualifier(String qualifier) {
        setElementComponent(0, 0, qualifier);
    }

    public void readSegmentGroup(SegmentSource edifactParser) throws IOException {
        contactInfo = edifactParser.readOptionalSegment(CtaSegment.class);
        edifactParser.readOptionalSegment(ComSegment.class);
    }

    @Override
    public void writeTo(EdifactSegmentWriter writer) throws IOException {
        writer.writeSegment(this);
        if (contactInfo != null) contactInfo.writeTo(writer);
    }

    public String getCity() {
        return getElementData(5);
    }

    public void setCity(String city) {
        setElementComponent(5, 0, city);
    }

    public String getCountry() {
        return getElementData(8);
    }

    public void setCountry(String country) {
        setElementComponent(8, 0, country);
    }

    public CtaSegment getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(CtaSegment contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getResponsibleAgency() {
        return getElementComponent(1, 2);
    }

    private void setResponsibleAgency(String agency) {
        setElementComponent(1, 2, agency);
    }

}
