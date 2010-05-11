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

    public void readSegmentGroup() {


    }

    public List<ErcSegment> getErrorCodes() {
        return errorCodes;
    }

    public void readSegmentGroup(SegmentSource edifactParser) throws IOException {
        beginMessage = edifactParser.readMandatorySegment(BgmSegment.class);

        messageDate = edifactParser.readMandatorySegment(DtmSegment.class, "137");
        arrivalTime = edifactParser.readMandatorySegment(DtmSegment.class, "178");

        referencedMessage = edifactParser.readMandatorySegment(RffSegment.class);
        edifactParser.readMandatorySegmentGroup(NadSegment.class, "FR");
        edifactParser.readMandatorySegmentGroup(NadSegment.class, "DO");
        edifactParser.readOptionalSegmentGroup(NadSegment.class, "C1");
        edifactParser.readOptionalSegmentGroup(NadSegment.class, "C2");

        ErcSegment ercSegment;
        while ((ercSegment = edifactParser.readOptionalSegmentGroup(ErcSegment.class)) != null) {
            errorCodes.add(ercSegment);
        }

        edifactParser.readOptionalSegment(RffSegment.class);
    }

    public BgmSegment getBeginMessage() {
        return beginMessage;
    }

    public RffSegment getReferencedMessage() {
        return referencedMessage;
    }

    public DtmSegment getMessageDate() {
        return messageDate;
    }

    public DtmSegment getArrivalTime() {
        return arrivalTime;
    }

}
