package no.statnett.larm.edifact;

import java.io.IOException;

public interface EdifactMessage {

    void write(EdifactSegmentWriter writer) throws IOException;

    String getMessageType();

    String getVersion();

    String getRelease();

    String getAgency();

    String getAssociatedCode();

}
