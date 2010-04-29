package no.statnett.larm.edifact;

import java.util.List;

public class EdifactSegment {

	private String segmentName;
	private List<EdifactDataElement> elements;

	public void setSegmentName(String segmentName) {
		this.segmentName = segmentName;
	}

	public String getSegmentName() {
		return segmentName;
	}

	List<EdifactDataElement> getDataElements() {
		return elements;
	}

	public void setDataElements(List<EdifactDataElement> elements) {
		this.elements = elements;
	}

	public String getElementData(int elementIndex) {
		return getDataElements().get(elementIndex).toString();
	}

	public String getElementComponent(int elementIndex, int componentIndex) {
		return getDataElements().get(elementIndex).getComponentData().get(componentIndex);
	}
}
