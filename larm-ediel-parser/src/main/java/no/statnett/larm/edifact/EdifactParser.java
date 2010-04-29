package no.statnett.larm.edifact;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class EdifactParser implements SegmentSource {

	private char segmentTerminator = '\'';
	private char dataElementSeparator = '+';
	private char componentDataElementSeparator = ':';
	private Map<String, Class<? extends EdifactSegment>> segmentClassMap = new HashMap<String, Class<? extends EdifactSegment>>();
	private String currentSegment;
	private String pushedBackSegment;
	private final Reader reader;
	private boolean hasReadFirst = false;

	public EdifactParser(Reader reader) throws IOException {
		this.reader = reader;
	}

	public EdifactSegment readEdifactSegment() throws IOException {
		String segment = readSegment();
		return segment != null ? parseSegment(segment) : null;
	}

	public void pushBack() {
		pushedBackSegment = currentSegment;
	}

	public EdifactSegment readOptionalSegment(String segmentName) throws IOException {
		EdifactSegment segment = readEdifactSegment();
		if (segment.getSegmentName().equals(segmentName)) return segment;
		pushBack();
		return null;
	}

	public EdifactSegment readMandatorySegment(String segmentName) throws IOException {
		EdifactSegment segment = readEdifactSegment();
		if (segment.getSegmentName().equals(segmentName)) return segment;
		pushBack();
		throw new RuntimeException("Required segment <" + segmentName + "> but was <" + segment.getSegmentName() + ">");
	}

	private String readSegment() throws IOException {
		if (pushedBackSegment != null) {
			currentSegment = pushedBackSegment;
			pushedBackSegment = null;
		} else {
			if (!hasReadFirst) {
				hasReadFirst = true;
				currentSegment = readFirstSegment();
			} else {
				currentSegment = readTo(segmentTerminator);
			}
			currentSegment = StringUtils.stripStart(currentSegment, " \n\t\r");
		}
		return currentSegment;
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

		if (segmentHeader.equals("UNA")) {
			String unaBody = read(6);
			segmentTerminator = unaBody.charAt(5);
			dataElementSeparator = unaBody.charAt(1);
			componentDataElementSeparator = unaBody.charAt(0);
			return segmentHeader + unaBody;
		} else {
			String segmentBody = readTo(segmentTerminator);
			if (segmentBody != null) {
				return segmentHeader+segmentBody;
			} else {
				return null;
			}
		}
	}

	EdifactSegment parseSegment(String segment) {
		EdifactSegment edifactSegment = createSegment(segment.substring(0,3));
		StringUtils.splitPreserveAllTokens(segment, dataElementSeparator);
		List<String> dataElements = splitToStringList(segment, dataElementSeparator);
		dataElements.remove(0);
		edifactSegment.setDataElements(parseElements(dataElements));
		return edifactSegment;
	}

	private List<EdifactDataElement> parseElements(List<String> dataElements) {
		ArrayList<EdifactDataElement> result = new ArrayList<EdifactDataElement>();
		for (String element : dataElements) {
			result.add(new EdifactDataElement(element, splitToStringList(element, componentDataElementSeparator)));
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
		while ((c = reader.read()) != -1) {
			if (c == terminator) return result.toString();
			result.append((char)c);
		}
		return null;
	}

	private String read(int characters) throws IOException {
		StringBuilder result = new StringBuilder();
		while (result.length() < characters) {
			int c = reader.read();
			result.append((char)c);
		}
		return result.toString();
	}

	private ArrayList<String> splitToStringList(String segment, char separator) {
		return new ArrayList<String>(Arrays.asList(StringUtils.splitPreserveAllTokens(segment, separator)));
	}

	public<T extends EdifactSegment> T readMandatorySegment(Class<T> segmentClass) throws IOException {
		T segment = readOptionalSegment(segmentClass);
		if (segment == null) {
			throw new RuntimeException("Required segment of type " + segmentClass + " - was " + currentSegment);
		}
		return segment;
	}

	public<T extends QualifiedEdifactSegment> T readMandatorySegment(Class<T> segmentClass, String qualifier) throws IOException {
		T segment = readOptionalSegment(segmentClass, qualifier);
		if (segment == null) {
			throw new RuntimeException("Required segment of type " + segmentClass + " with qualifier " + qualifier + " - was " + currentSegment);
		}
		return segment;
	}

	public<T extends EdifactSegment> T readOptionalSegment(Class<T> segmentClass) throws IOException {
		if (segmentClass.getAnnotation(Segment.class) == null) {
			throw new IllegalArgumentException(segmentClass + " must be annotated with " + Segment.class);
		}
		String segment = readSegment();
		if (!segment.substring(0,3).equals(segmentClass.getAnnotation(Segment.class).value())) {
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
		edifactSegment.setSegmentName(segment.substring(0,3));
		StringUtils.splitPreserveAllTokens(segment, dataElementSeparator);
		List<String> dataElements = splitToStringList(segment, dataElementSeparator);
		dataElements.remove(0);
		edifactSegment.setDataElements(parseElements(dataElements));
		return edifactSegment;
	}

	public<T extends QualifiedEdifactSegment> T readOptionalSegment(Class<T> segmentClass, String qualifier) throws IOException {
		T segment = readOptionalSegment(segmentClass);
		if (segment == null) return null;
		if (!segment.getQualifier().equals(qualifier)) {
			pushBack();
			return null;
		}
		return segment;
	}

}
