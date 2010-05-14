package no.statnett.larm.edifact;

import java.io.IOException;

public interface EdifactMessage {

    void write(Appendable writer) throws IOException;

}
