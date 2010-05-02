package no.statnett.larm.edifact;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EdifactParser implements SegmentSource {

	private Map<String, Class<? extends EdifactSegment>> segmentClassMap = new HashMap<String, Class<? extends EdifactSegment>>();

	private final ParserConfig ctx = new ParserConfig();
	private final EdifactLexer lexer;

	public EdifactParser(final Reader input) throws IOException {
		this.lexer = new EdifactLexer(ctx, input, 30);
	}

	public EdifactParser(final String input) throws IOException {
		this(new StringReader(input));
	}

	EdifactSegment readEdifactSegment() throws IOException {
		String segment = lexer.readSegment();
		return segment != null ? parseSegment(segment) : null;
	}

	void pushBack() {
		lexer.pushBack();
	}

	public EdifactSegment readOptionalSegment(String segmentName) throws IOException {
		EdifactSegment segment = readEdifactSegment();
		if (segment.getSegmentName().equals(segmentName))
			return segment;
		pushBack();
		return null;
	}

	public EdifactSegment readMandatorySegment(String segmentName) throws IOException {
		EdifactSegment segment = readEdifactSegment();
		if (segment.getSegmentName().equals(segmentName))
			return segment;
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

	EdifactSegment parseSegment(String segment) {
		EdifactSegment edifactSegment = createSegment(lexer.getSegmentHeader(segment));
		List<String> dataElements = lexer.getDataElements(segment);
		dataElements.remove(0);
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

	private EdifactSegment createSegment(String segmentName) {
		EdifactSegment edifactSegment = new EdifactSegment();
		if (segmentClassMap.containsKey(segmentName)) {
			try {
				edifactSegment = segmentClassMap.get(segmentName).newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		edifactSegment.setSegmentName(segmentName);
		return edifactSegment;
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
		String segment = lexer.readSegment();
		String segmentHeader = lexer.getSegmentHeader(segment);

		if (!segmentHeader.equals(segmentClass.getAnnotation(Segment.class).value())) {
			pushBack();
			return null;
		}

		T edifactSegment = null;
		try {
			edifactSegment = segmentClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		edifactSegment.setSegmentName(segmentHeader);
		List<String> dataElements = lexer.getDataElements(segment);
		dataElements.remove(0);
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

}
