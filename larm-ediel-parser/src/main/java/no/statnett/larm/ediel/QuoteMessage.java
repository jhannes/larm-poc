package no.statnett.larm.ediel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import no.statnett.larm.edifact.EdifactMessage;
import no.statnett.larm.edifact.EdifactSegmentWriter;
import no.statnett.larm.edifact.SegmentSource;

public class QuoteMessage implements EdifactMessage {

    private BgmSegment beginMessage;
    private DtmSegment messageDate;
    private DtmSegment processingStartTime;
    private DtmSegment processingEndTime;
    private DtmSegment offsetToUTC;
    private CuxSegment currency;
    private NadSegment messageFrom;
    private NadSegment documentRecipient;
    private List<LinSegment> lineItems = new ArrayList<LinSegment>();

    public BgmSegment getBeginMessage() {
        return beginMessage;
    }

    public DtmSegment getMessageDate() {
        return messageDate;
    }

    public DtmSegment getProcessingStartTime() {
        return processingStartTime;
    }

    public DtmSegment getProcessingEndTime() {
        return processingEndTime;
    }

    public DtmSegment getOffsetToUTC() {
        return offsetToUTC;
    }

    public CuxSegment getCurrency() {
        return currency;
    }

    public NadSegment getMessageFrom() {
        return messageFrom;
    }

    public NadSegment getDocumentRecipient() {
        return documentRecipient;
    }

    public List<LinSegment> getLineItems() {
        return lineItems;
    }

    public void addLineItem(LinSegment segment) {
        lineItems.add(segment);
    }

    public void readSegmentGroup(SegmentSource segmentSource) throws IOException {
        beginMessage = segmentSource.readMandatorySegment(BgmSegment.class);
        messageDate = segmentSource.readOptionalSegment(DtmSegment.class, "137");
        processingStartTime = segmentSource.readOptionalSegment(DtmSegment.class, "163");
        processingEndTime = segmentSource.readOptionalSegment(DtmSegment.class, "164");
        offsetToUTC = segmentSource.readOptionalSegment(DtmSegment.class, "ZZZ");
        currency = segmentSource.readMandatorySegment(CuxSegment.class);

        messageFrom = segmentSource.readMandatorySegmentGroup(NadSegment.class, "FR");
        documentRecipient = segmentSource.readMandatorySegmentGroup(NadSegment.class, "DO");

        LinSegment linSegment;
        while ((linSegment = segmentSource.readOptionalSegmentGroup(LinSegment.class)) != null) {
            lineItems.add(linSegment);
        }
    }

    @Override
    public void write(EdifactSegmentWriter writer) throws IOException {
        writer.writeSegment(beginMessage);
        writer.writeSegment(messageDate);
        writer.writeSegment(processingStartTime);
        writer.writeSegment(processingEndTime);
        writer.writeSegment(offsetToUTC);
        writer.writeSegment(currency);
        writer.writeSegment(messageFrom);
        writer.writeSegment(documentRecipient);

        for (LinSegment linSegment : lineItems) {
            linSegment.writeTo(writer);
        }
    }

    @Override
    public String getAgency() {
        return "UN";
    }

    @Override
    public String getAssociatedCode() {
        return "EDIEL2";
    }

    @Override
    public String getMessageType() {
        return "QUOTES";
    }

    @Override
    public String getRelease() {
        return "96A";
    }

    @Override
    public String getVersion() {
        return "D";
    }

}

