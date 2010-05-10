package no.statnett.larm.ediel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import no.statnett.larm.edifact.EdifactSegment;
import no.statnett.larm.edifact.Segment;
import no.statnett.larm.edifact.SegmentSource;

@Segment("LIN")
public class LinSegment extends EdifactSegment {

    private DtmSegment restingTime;
    private DtmSegment availability;
    private List<PriSegment> priceDetails = new ArrayList<PriSegment>();
    private RffSegment priceQuote;
    private LocSegment location;

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
        restingTime = segmentSource.readOptionalSegment(DtmSegment.class, "66");

        PriSegment priSegment;
        while ((priSegment = segmentSource.readOptionalSegment(PriSegment.class)) != null) {
            priSegment.readSegmentGroup(segmentSource);
            priceDetails.add(priSegment);
        }

        priceQuote = segmentSource.readMandatorySegment(RffSegment.class, "PR");
        location = segmentSource.readMandatorySegment(LocSegment.class, "90");
    }

	public void setLocation(LocSegment locSegment) {
		this.location = locSegment;
	}


}
