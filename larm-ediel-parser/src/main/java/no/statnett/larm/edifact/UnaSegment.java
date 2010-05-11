package no.statnett.larm.edifact;

import java.io.IOException;
import java.io.Writer;

public class UnaSegment extends EdifactSegment {

    private String serviceAdviceString;

    public UnaSegment() {
        setSegmentName("UNA");
    }

    public UnaSegment(String serviceAdviceString) {
        setSegmentName("UNA");
        this.serviceAdviceString = serviceAdviceString;
    }

    @Override
    public void write(Writer writer) throws IOException {
        writer.write(getSegmentName() + serviceAdviceString + "\n");
    }
}
