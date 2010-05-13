package no.statnett.larm.ediel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import no.statnett.larm.edifact.SegmentSource;

public class AperakMessage {

    private BgmSegment beginMessage;
    private RffSegment referencedMessage;
    private DtmSegment messageDate;
    private DtmSegment arrivalTime;
    private List<ErcSegment> errorCodes = new ArrayList<ErcSegment>();

    public List<ErcSegment> getErrorCodes() {
        return errorCodes;
    }

    public void readSegmentGroup(SegmentSource edifactParser) throws IOException {
        beginMessage = edifactParser.readMandatorySegment(BgmSegment.class);

        messageDate = edifactParser.readMandatorySegment(DtmSegment.class, "137");
        arrivalTime = edifactParser.readMandatorySegment(DtmSegment.class, "178");

        referencedMessage = edifactParser.readMandatorySegment(RffSegment.class);
        edifactParser.readOptionalSegmentGroup(NadSegment.class, "FR");
        edifactParser.readOptionalSegmentGroup(NadSegment.class, "DO");
        edifactParser.readOptionalSegmentGroup(NadSegment.class, "C1");
        edifactParser.readOptionalSegmentGroup(NadSegment.class, "C2");

        ErcSegment ercSegment;
        while ((ercSegment = edifactParser.readOptionalSegmentGroup(ErcSegment.class)) != null) {
            errorCodes.add(ercSegment);
        }

        edifactParser.readOptionalSegment(RffSegment.class);
    }

    public void write(Appendable writer) throws IOException {
        beginMessage.write(writer);
        messageDate.write(writer);
        arrivalTime.write(writer);
        referencedMessage.write(writer);
    }

    public BgmSegment getBeginMessage() {
        return beginMessage;
    }

    public void setBeginMessage(BgmSegment beginMessage) {
        this.beginMessage = beginMessage;
    }

    public RffSegment getReferencedMessage() {
        return referencedMessage;
    }

    public void setReferencedMessage(RffSegment referencedMessage) {
        this.referencedMessage = referencedMessage;
    }

    public DtmSegment getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(DtmSegment messageDate) {
        this.messageDate = messageDate;
        messageDate.setQualifier("137");
    }

    public DtmSegment getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(DtmSegment arrivalTime) {
        this.arrivalTime = arrivalTime;
        arrivalTime.setQualifier("178");
    }

}
