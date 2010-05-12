package no.statnett.larm.edifact;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class EdifactSegment {

	ParserContext ctx;
	int lineNum, columnNum, segmentNum;
	String segmentBody;

	private List<EdifactDataElement> elements;
	private String segmentName;

	public EdifactSegment() {
	}

	public EdifactSegment(String segmentName, String segmentBody) {
		this.segmentName = segmentName;
		this.segmentBody = segmentBody;
	}

	List<EdifactDataElement> getDataElements() {
		if (elements == null) {
			elements = new ArrayList<EdifactDataElement>();
		}
		return elements;
	}

	public String getElementComponent(int elementIndex, int componentIndex) {
		return getDataElements().get(elementIndex).getComponentData().get(
				componentIndex);
	}

	public void setElementComponent(int elementIndex, int componentIndex,
			String data) {
		while (getDataElements().size() <= elementIndex) {
			getDataElements().add(new EdifactDataElement());
		}
		List<String> componentData = getDataElements().get(elementIndex)
				.getComponentData();
		while (componentData.size() <= componentIndex) {
			componentData.add("");
		}
		componentData.set(componentIndex, data);
	}

	protected EdifactDataElement getDataElement(int index) {
		List<EdifactDataElement> lst = getDataElements();
		return (index < lst.size()) ? lst.get(index) : null;
	}

	public String getElementData(int index) {
		EdifactDataElement dataElement = getDataElement(index);
		return dataElement == null ? null : dataElement.toString();
	}

	public String getSegmentName() {
		if (segmentName == null) {
			Segment annotation = getClass().getAnnotation(Segment.class);
			if (annotation != null)
				segmentName = annotation.value();
		}
		return segmentName;
	}

	public void setDataElements(List<EdifactDataElement> elements) {
		this.elements = elements;
	}

	public void setSegmentName(String segmentName) {
		this.segmentName = segmentName;
	}

	public void write(Writer writer) throws IOException {
		writer.append(getSegmentName());
		for (EdifactDataElement element : getDataElements()) {
			// TODO: Get from ParserContext
			writer.append("+");
			boolean first = true;
			for (String component : element.getComponentData()) {
				if (!first)
					writer.append(":");
				writer.append(component);
				first = false;
			}
		}
		writer.append("'\n");
	}

	@Override
	public String toString() {
		return segmentName;
	}
}
