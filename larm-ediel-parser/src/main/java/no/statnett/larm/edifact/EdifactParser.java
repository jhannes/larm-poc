package no.statnett.larm.edifact;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EdifactParser implements SegmentSource {

    private Map<String, Class<? extends EdifactSegment>> segmentClassMap = new HashMap<String, Class<? extends EdifactSegment>>();

    private final ParserContext ctx = new ParserContext();
    private final EdifactLexer lexer;

    public EdifactParser(final Reader input) throws IOException {
        this.lexer = new EdifactLexer(ctx, input, 30);
    }

    EdifactSegment readEdifactSegment() throws IOException {
        EdifactSegment segment = lexer.readSegment();
        return segment != null ? parseSegment(segment) : null;
    }

    void pushBack() {
        lexer.pushBack();
    }

    public EdifactSegment readOptionalSegment(String segmentName) throws IOException {
        EdifactSegment segment = readEdifactSegment();
        if (segment == null) return null;
        if (segment.getSegmentName().equals(segmentName))
            return segment;
        pushBack();
        return null;
    }

    public EdifactSegment readMandatorySegment(String segmentName) throws IOException {
        EdifactSegment segment = readEdifactSegment();
        if (segment != null && segment.getSegmentName().equals(segmentName))
            return segment;
        pushBack();
        throw new EdifactParserException(lexer.formatPosition(), "Required segment <" + segmentName + "> but was <"
                + segment + ">");
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
        edifactSegment.setDataElements(parseElements(dataElements));
        return edifactSegment;
    }

    private List<EdifactDataElement> parseElements(final List<String> dataElements) {

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

    public <T extends QualifiedEdifactSegment> T readMandatorySegment(Class<T> segmentClass, String qualifier) throws IOException {
        T segment = readOptionalSegment(segmentClass, qualifier);
        if (segment == null) {
            String s = "Required segment of type " + segmentClass + " with qualifier " + qualifier + " - was " + lexer.getSegment();
            throw new EdifactParserException(lexer.formatPosition(), s);
        }
        return segment;
    }

    public <T extends EdifactSegment> T readOptionalSegment(Class<T> segmentClass) throws IOException {
        if (segmentClass.getAnnotation(Segment.class) == null) {
            throw new IllegalArgumentException(segmentClass + " must be annotated with " + Segment.class);
        }
        EdifactSegment basicSegment = lexer.readSegment();

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
        edifactSegment.setSegmentName(basicSegment.getSegmentName());
        List<String> dataElements = lexer.getDataElements(basicSegment);
        edifactSegment.setDataElements(parseElements(dataElements));
        return edifactSegment;
    }

    public <T extends QualifiedEdifactSegment> T readOptionalSegment(Class<T> segmentClass, String qualifier) throws IOException {
        T segment = readOptionalSegment(segmentClass);
        if (segment == null)
            return null;
        if (!segment.getQualifier().equals(qualifier)) {
            pushBack();
            return null;
        }
        return segment;
    }

    @Override
    public <T extends QualifiedEdifactSegmentGroup> T readMandatorySegmentGroup(Class<T> segmentClass, String qualifier) throws IOException {
        return readSegmentGroup(readMandatorySegment(segmentClass, qualifier));
    }

    @Override
    public <T extends QualifiedEdifactSegmentGroup> T readOptionalSegmentGroup(Class<T> segmentClass, String qualifier) throws IOException {
        return readSegmentGroup(readOptionalSegment(segmentClass, qualifier));
    }

    @Override
    public <T extends EdifactSegmentGroup> T readOptionalSegmentGroup(Class<T> segmentClass) throws IOException {
        return readSegmentGroup(readOptionalSegment(segmentClass));
    }

    private <T extends SegmentGroup> T readSegmentGroup(T segmentGroup) throws IOException {
        if (segmentGroup != null) segmentGroup.readSegmentGroup(this);
        return segmentGroup;
    }

}
