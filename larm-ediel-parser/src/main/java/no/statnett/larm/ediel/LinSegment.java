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
    private DtmSegment availability;
    private List<PriSegment> priceDetails = new ArrayList<PriSegment>();
    private RffSegment priceQuote;
    private LocSegment location;
    private DtmSegment duration;

    public String getItemNumber() {
        return getElementComponent(2, 0);
    }

    public String getResponsibleAgency() {
        return getElementComponent(2, 3);
    }

    public DtmSegment getAvailability() {
        return availability;
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
        availability = segmentSource.readOptionalSegment(DtmSegment.class, "44");
        duration = segmentSource.readOptionalSegment(DtmSegment.class, "48");
        restingTime = segmentSource.readOptionalSegment(DtmSegment.class, "66");

        PriSegment priSegment;
        while ((priSegment = segmentSource.readOptionalSegmentGroup(PriSegment.class)) != null) {
            priceDetails.add(priSegment);
        }

        priceQuote = segmentSource.readMandatorySegment(RffSegment.class, "PR");
        location = segmentSource.readMandatorySegment(LocSegment.class, "90");
    }

    public void setLocation(LocSegment locSegment) {
        this.location = locSegment;
    }

    public void setPriceQuote(RffSegment rffSegment) {
        this.priceQuote = rffSegment;
        this.priceQuote.setQualifier("PR");
    }

    public void setAvailability(DtmSegment availability) {
        this.availability = availability;
        this.availability.setQualifier("44");
    }

    public void setDuration(DtmSegment duration) {
        this.duration = duration;
        this.duration.setQualifier("48");
    }

    public DtmSegment getDuration() {
        return duration;
    }

    public void setRestingTime(DtmSegment restingTime) {
        this.restingTime = restingTime;
        this.restingTime.setQualifier("66");
    }
}
