package no.statnett.larm.edifact;

import java.io.IOException;

public class EdifactSegmentWriter {

    private final Appendable writer;
    private int segmentCount;

    public EdifactSegmentWriter(Appendable writer) {
        this.writer = writer;
    }

    public int getSegmentCount() {
        return segmentCount;
    }

    public void writeSegment(EdifactSegment segment) throws IOException {
        segmentCount++;
        writer.append(segment.getSegmentName());
        for (EdifactDataElement element : segment.getDataElements()) {
            // TODO: Get from ParserContext
            writer.append("+");
            boolean first = true;
            for (String component : element.getComponentData()) {
                if (!first) writer.append(":");
                writer.append(component);
                first = false;
            }
        }
        writer.append("'\n");
    }

    public void resetSegmentCount() {
        segmentCount = 0;
    }

}
