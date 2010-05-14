package no.statnett.larm.edifact;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.io.StringWriter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;

public class EdifactWriterTest {

    @Test
    public void shouldCountSegments() throws Exception {
        EdifactInterchange interchange = new EdifactInterchange(createEdifactMessageWithSegments(3));
        assertThat(serialize(interchange)).contains("UNT+5+1'");

        interchange = new EdifactInterchange(createEdifactMessageWithSegments(20));
        assertThat(serialize(interchange)).contains("UNT+22+1'");
    }

    @Test
    public void shouldWriteMessageName() throws Exception {
        EdifactInterchange interchange = new EdifactInterchange(createMessageOfTypeAndVersion("APERAK", "D", "96A", "UN", "EDIEL2"));
        assertThat(serialize(interchange)).contains("UNH+1+APERAK:D:96A:UN:EDIEL2'");

        interchange = new EdifactInterchange(createMessageOfTypeAndVersion("QUOTES", "C", "96R", "FBI", "EDIEL8"));
        assertThat(serialize(interchange)).contains("UNH+1+QUOTES:C:96R:FBI:EDIEL8'");
    }

    @Test
    public void shouldWriteUNB() throws Exception {
        EdifactInterchange interchange = new EdifactInterchange(createEdifactMessageWithSegments(2));
        interchange.setSyntax("UNOD", "8");
        interchange.setSender("708000501111", "14", "");
        interchange.setRecipient("708000509999", "14", "REGULERKRAFT2");
        assertThat(serialize(interchange))
            .contains("UNB+UNOD:8+708000501111:14+708000509999:14:REGULERKRAFT2");
    }

    @Test
    public void shouldWriteTimestampAndReference() throws Exception {
        DateTime now = new DateTime(2010, 3, 13, 1, 44, 0, 0);
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());
        EdifactInterchange interchange = new EdifactInterchange(createEdifactMessageWithSegments(2));
        interchange.setControlReference("abc1234");
        assertThat(serialize(interchange))
            .contains("UNB++++100313:0144+abc1234")
            .contains("UNZ+1+abc1234");

    }

    private EdifactMessage createMessageOfTypeAndVersion(final String messageType, final String version, final String release, final String agency, final String associatedCode) {
        return new EdifactMessage() {
            @Override
            public void write(EdifactSegmentWriter writer) throws IOException {
                new EdifactSegment("FOO", "").write(writer);
            }

            @Override
            public String getAgency() {
                return agency;
            }

            @Override
            public String getAssociatedCode() {
                return associatedCode;
            }

            @Override
            public String getMessageType() {
                return messageType;
            }

            @Override
            public String getRelease() {
                return release;
            }

            @Override
            public String getVersion() {
                return version;
            }
        };
    }

    private String serialize(EdifactInterchange interchange) throws IOException {
        StringWriter interchangeAsString = new StringWriter();
        interchange.write(interchangeAsString);
        return interchangeAsString.toString();
    }

    private EdifactMessage createEdifactMessageWithSegments(final int segmentCount) {
        return new EdifactMessage() {
            @Override
            public void write(EdifactSegmentWriter writer) throws IOException {
                for (int i=0; i<segmentCount; i++) {
                    new EdifactSegment("FOO", "").write(writer);
                }
            }

            @Override
            public String getAgency() {
                return null;
            }

            @Override
            public String getAssociatedCode() {
                return null;
            }

            @Override
            public String getMessageType() {
                return null;
            }

            @Override
            public String getRelease() {
                return null;
            }

            @Override
            public String getVersion() {
                return null;
            }
        };
    }

}
