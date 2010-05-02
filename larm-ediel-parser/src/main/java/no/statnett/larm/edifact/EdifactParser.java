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

	private final Reader reader;
	private final ParserContext ctx = new ParserContext(30);

	public EdifactParser(final Reader input) throws IOException {
		if (null == input) {
			throw new IllegalArgumentException("input cannot be null");
		}
		this.reader = input;
	}

	public EdifactParser(final String input) throws IOException {
		this(new StringReader(input));
	}

	EdifactSegment readEdifactSegment() throws IOException {
		String segment = readSegment();
		return segment != null ? parseSegment(segment) : null;
	}

	void pushBack() {
		ctx.pushBack();
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
		throw new EdifactParserException(ctx, "Required segment <" + segmentName + "> but was <" + segment.getSegmentName() + ">");
	}

	private String readSegment() throws IOException {
		if (ctx.popCurrent() == null) {
			if (!ctx.hasReadFirst) {
				ctx.hasReadFirst = true;
				ctx.currentSegment = readFirstSegment();
			} else {
				ctx.currentSegment = readTo(ctx.segmentTerminator);
			}
			ctx.currentSegment = stripStart(ctx.currentSegment, " \n\t\r");
		}
		return ctx.currentSegment;
	}

	private String stripStart(String str, String stripChars) {
		if (str == null)
			return null;

		int len = str.length();
		for (int i = 0; i < len; i++) {
			if (stripChars.indexOf(str.charAt(i)) == -1) {
				return str.substring(i, len);
			}
		}
		return str;
	}

	public Iterable<EdifactSegment> eachSegment() throws IOException {
		ArrayList<EdifactSegment> result = new ArrayList<EdifactSegment>();
		EdifactSegment segment;
		while ((segment = readEdifactSegment()) != null) {
			result.add(segment);
		}
		return result;
	}

	private String readFirstSegment() throws IOException {
		String segmentHeader = read(3);

		if ("UNA".equals(segmentHeader)) {
			String unaBody = read(6);
			ctx.initSeparators(unaBody);
			return segmentHeader + unaBody;
		} else {
			String segmentBody = readTo(ctx.segmentTerminator);
			if (segmentBody != null) {
				return segmentHeader + segmentBody;
			} else {
				return null;
			}
		}
	}

	EdifactSegment parseSegment(String segment) {
		EdifactSegment edifactSegment = createSegment(segment.subSequence(0, 3).toString());
		List<String> dataElements = splitToStringList(segment, ctx.dataElementSeparator, ctx.releaseIndicator);
		dataElements.remove(0);
		edifactSegment.setDataElements(parseElements(dataElements));
		return edifactSegment;
	}

	private List<EdifactDataElement> parseElements(final List<String> dataElements) {

		List<EdifactDataElement> result = new ArrayList<EdifactDataElement>();
		for (String element : dataElements) {
			List<String> tokens = splitToStringList(element, ctx.componentDataElementSeparator, ctx.releaseIndicator);
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

	private String readTo(char terminator) throws IOException {
		StringBuilder result = new StringBuilder();
		int c;

		while ((c = ctx.readChar(reader)) != -1) {
			if (c == terminator)
				return result.toString();
			result.append((char) c);
		}
		return null;
	}

	private String read(int characters) throws IOException {
		StringBuilder result = new StringBuilder(characters);

		int c;
		while (result.length() < characters && ((c = ctx.readChar(reader)) != -1)) {
			result.append((char) c);
		}
		return result.toString();
	}

	List<String> splitToStringList(CharSequence text, char separator, char escape) {
		List<String> tokens = new ArrayList<String>(6);
		int len;

		if (text == null || (len = text.length()) == 0) {
			return tokens;
		}

		int start = 0;
		StringBuilder tempToken = new StringBuilder();

		for (int i = 0; i < len; i++) {
			char c = text.charAt(i);
			if (c == separator) {
				tempToken.append(text.subSequence(start, i));
				tokens.add(tempToken.toString());
				tempToken.delete(0, tempToken.length());
				start = i + 1;
			} else if (c == escape) {
				if (i < (len - 1) && text.charAt(i + 1) == separator) {
					tempToken.append(text.subSequence(start, i));
					start = ++i; // skip separator next char
				}
			}
		}

		if (start <= len) {
			tempToken.append(text.subSequence(start, len));
			tokens.add(tempToken.toString());
		}

		return tokens;
	}

	public <T extends EdifactSegment> T readMandatorySegment(Class<T> segmentClass) throws IOException {
		T segment = readOptionalSegment(segmentClass);
		if (segment == null) {
			throw new EdifactParserException(ctx, "Required segment of type " + segmentClass + " - was " + ctx.currentSegment);
		}
		return segment;
	}

	public <T extends QualifiedEdifactSegment> T readMandatorySegment(Class<T> segmentClass, String qualifier) throws IOException {
		T segment = readOptionalSegment(segmentClass, qualifier);
		if (segment == null) {
			throw new EdifactParserException(ctx, "Required segment of type " + segmentClass + " with qualifier " + qualifier
					+ " - was " + ctx.currentSegment);
		}
		return segment;
	}

	public <T extends EdifactSegment> T readOptionalSegment(Class<T> segmentClass) throws IOException {
		if (segmentClass.getAnnotation(Segment.class) == null) {
			throw new IllegalArgumentException(segmentClass + " must be annotated with " + Segment.class);
		}
		String segment = readSegment();
		String segmentHeader = segment.substring(0, 3);

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
		List<String> dataElements = splitToStringList(segment, ctx.dataElementSeparator, ctx.releaseIndicator);
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
