package no.statnett.larm.ediel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import no.statnett.larm.edifact.EdifactSegmentGroup;
import no.statnett.larm.edifact.Segment;
import no.statnett.larm.edifact.SegmentSource;

@Segment("LIN")
public class LinSegment extends EdifactSegmentGroup {

    private DtmSegment restingTime;
    private DtmSegment maxDuration;
    private List<PriSegment> priceDetails = new ArrayList<PriSegment>();
    private RffSegment priceQuote;
    private LocSegment location;
    private DtmSegment minDuration;
    @SuppressWarnings("unused")
    private DtmSegment activationTime;
    @SuppressWarnings("unused")
    private DtmSegment processingTime;
    @SuppressWarnings("unused")
    private RffSegment additionalReferenceNumber;
    @SuppressWarnings("unused")
    private RffSegment relatedDocumentNumber;

    public String getItemNumber() {
        return getElementComponent(2, 0);
    }

    public String getResponsibleAgency() {
        return getElementComponent(2, 3);
    }

    public DtmSegment getAvailability() {
        return maxDuration;
    }

    public DtmSegment getRestingTime() {
        return restingTime;
    }

    public List<PriSegment> getPriceDetails() {
        return priceDetails;
    }

    public void addPriceSegment(PriSegment segment) {
        priceDetails.add(segment);
    }

    public RffSegment getPriceQuote() {
        return priceQuote;
    }

    public LocSegment getLocation() {
        return location;
    }

    public void readSegmentGroup(SegmentSource segmentSource) throws IOException {
        maxDuration = segmentSource.readOptionalSegment(DtmSegment.class, "44");
        minDuration = segmentSource.readOptionalSegment(DtmSegment.class, "48");
        restingTime = segmentSource.readOptionalSegment(DtmSegment.class, "66");
        activationTime = segmentSource.readOptionalSegment(DtmSegment.class, "163");
        processingTime = segmentSource.readOptionalSegment(DtmSegment.class, "324");

        PriSegment priSegment;
        while ((priSegment = segmentSource.readOptionalSegmentGroup(PriSegment.class)) != null) {
            priceDetails.add(priSegment);
        }

        priceQuote = segmentSource.readOptionalSegment(RffSegment.class, "PR");
        relatedDocumentNumber = segmentSource.readOptionalSegment(RffSegment.class, "ACE");
        additionalReferenceNumber = segmentSource.readOptionalSegment(RffSegment.class, "ACD");
        location = segmentSource.readOptionalSegment(LocSegment.class, "90");
    }

    public void setLocation(LocSegment locSegment) {
        this.location = locSegment;
    }

    public void setPriceQuote(RffSegment rffSegment) {
        this.priceQuote = rffSegment;
        this.priceQuote.setQualifier("PR");
    }

    public void setAvailability(DtmSegment availability) {
        this.maxDuration = availability;
        this.maxDuration.setQualifier("44");
    }

    public void setDuration(DtmSegment duration) {
        this.minDuration = duration;
        this.minDuration.setQualifier("48");
    }

    public DtmSegment getDuration() {
        return minDuration;
    }

    public void setRestingTime(DtmSegment restingTime) {
        this.restingTime = restingTime;
        this.restingTime.setQualifier("66");
    }
}
