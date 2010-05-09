package no.statnett.larm.edifact;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EdifactParser implements SegmentSource {

    private Map<String, Class<? extends EdifactSegment>> segmentClassMap;

    {
        Map<String, Class<? extends EdifactSegment>> map = new HashMap<String, Class<? extends EdifactSegment>>();
        map.put("UNB", InterchangeHeader.class);
        map.put("UNZ", InterchangeTrailer.class);
        map.put("UNH", MessageHeader.class);
        map.put("UNT", MessageTrailer.class);
        segmentClassMap = map;
    }

    private final ParserContext ctx = new ParserContext();
    private final EdifactLexer lexer;

    public EdifactParser(final Reader input) throws IOException {
        this.lexer = new EdifactLexer(ctx, input, 30);
    }

    public EdifactParser(final String input) throws IOException {
        this(new StringReader(input));
    }

    EdifactSegment readEdifactSegment() throws IOException {
        EdifactSegment segment = lexer.readSegment();
        return segment != null ? parseSegment(segment) : null;
    }

    void pushBack() {
        lexer.pushBack();
    }

    @SuppressWarnings("unchecked")
    public <T extends EdifactSegment> T readOptionalSegment(String segmentName) throws IOException {
        EdifactSegment segment = readEdifactSegment();
        if (segment.getSegmentName().equals(segmentName))
            return (T) segment;
        pushBack();
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends EdifactSegment> T readMandatorySegment(String segmentName) throws IOException {
        EdifactSegment segment = readEdifactSegment();
        if (segment.getSegmentName().equals(segmentName))
            return (T) segment;
        pushBack();
        throw new EdifactParserException(lexer.formatPosition(), "Required segment <" + segmentName + "> but was <"
                + segment.getSegmentName() + ">");
    }

    public Iterable<EdifactSegment> eachSegment() throws IOException {
        ArrayList<EdifactSegment> result = new ArrayList<EdifactSegment>();
        EdifactSegment segment;
        while ((segment = readEdifactSegment()) != null) {
            result.add(segment);
        }
        return result;
    }

    EdifactSegment parseSegment(EdifactSegment segment) {
        EdifactSegment edifactSegment = initSegmentClass(segment);
        List<String> dataElements = lexer.getDataElements(segment);
        edifactSegment.setDataElements(parseElements(segment, dataElements));
        return edifactSegment;
    }

    private List<EdifactDataElement> parseElements(final EdifactSegment segment, final List<String> dataElements) {

        List<EdifactDataElement> result = new ArrayList<EdifactDataElement>();
        for (String element : dataElements) {
            List<String> tokens = lexer.getDataElementComponents(element);
            result.add(new EdifactDataElement(element, tokens));
        }
        return result;
    }

    private EdifactSegment initSegmentClass(EdifactSegment basicSegment) {
        EdifactSegment edifactSegment = basicSegment;
        String segmentName = basicSegment.getSegmentName();
        if (segmentClassMap.containsKey(segmentName)) {
            try {
                edifactSegment = segmentClassMap.get(segmentName).newInstance();
                copySegmentDetailsAcross(basicSegment, edifactSegment);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return edifactSegment;
    }

    private void copySegmentDetailsAcross(EdifactSegment fromSegment, EdifactSegment toSegment) {
        toSegment.setSegmentName(fromSegment.getSegmentName());
        toSegment.segmentBody = fromSegment.segmentBody;
        toSegment.lineNum = fromSegment.lineNum;
        toSegment.columnNum = fromSegment.columnNum;
        toSegment.segmentNum = fromSegment.segmentNum;
        toSegment.ctx = fromSegment.ctx;
        toSegment.setDataElements(fromSegment.getDataElements());
    }

    public <T extends EdifactSegment> T readMandatorySegment(Class<T> segmentClass) throws IOException {
        T segment = readOptionalSegment(segmentClass);
        if (segment == null) {
            String s = "Required segment of type " + segmentClass + " - was " + lexer.getSegment();
            throw new EdifactParserException(lexer.formatPosition(), s);
        }
        return segment;
    }

    public <T extends QualifiedEdifactSegment> T readMandatorySegment(Class<T> segmentClass, String qualifier)
            throws IOException {
        T segment = readOptionalSegment(segmentClass, qualifier);
        if (segment == null) {
            String s = "Required segment of type " + segmentClass + " with qualifier " + qualifier + " - was "
                    + lexer.getSegment();
            throw new EdifactParserException(lexer.formatPosition(), s);
        }
        return segment;
    }

    public <T extends EdifactSegment> T readOptionalSegment(Class<T> segmentClass) throws IOException {
        if (segmentClass.getAnnotation(Segment.class) == null) {
            throw new IllegalArgumentException(segmentClass + " must be annotated with " + Segment.class);
        }
        EdifactSegment basicSegment = readEdifactSegment();

        if (!basicSegment.getSegmentName().equals(segmentClass.getAnnotation(Segment.class).value())) {
            pushBack();
            return null;
        }

        T edifactSegment = null;
        try {
            edifactSegment = segmentClass.newInstance();
            copySegmentDetailsAcross(basicSegment, edifactSegment);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return edifactSegment;
    }

    public <T extends QualifiedEdifactSegment> T readOptionalSegment(Class<T> segmentClass, String qualifier)
            throws IOException {
        T segment = readOptionalSegment(segmentClass);
        if (segment == null)
            return null;
        if (!segment.getQualifier().equals(qualifier)) {
            pushBack();
            return null;
        }
        return segment;
    }

}
