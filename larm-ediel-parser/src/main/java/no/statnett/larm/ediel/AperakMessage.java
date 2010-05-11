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
        edifactParser.readMandatorySegment(NadSegment.class, "FR").readSegmentGroup(edifactParser);
        edifactParser.readMandatorySegment(NadSegment.class, "DO").readSegmentGroup(edifactParser);
        //edifactParser.readOptionalSegment(NadSegment.class, "C1").readSegmentGroup(edifactParser);
        //edifactParser.readOptionalSegment(NadSegment.class, "C2").readSegmentGroup(edifactParser);

        ErcSegment ercSegment;
        while ((ercSegment = edifactParser.readOptionalSegment(ErcSegment.class)) != null) {
            ercSegment.readSegmentGroup(edifactParser);
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
