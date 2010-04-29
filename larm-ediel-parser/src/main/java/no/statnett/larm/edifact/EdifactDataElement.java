package no.statnett.larm.edifact;

import java.util.ArrayList;
import java.util.List;

public class EdifactDataElement {

	private final ArrayList<String> componentData;
	private String dataElement;

	public EdifactDataElement(String dataElement, ArrayList<String> componentData) {
		this.dataElement = dataElement;
		this.componentData = componentData;
	}

	public List<String> getComponentData() {
		return componentData;
	}

	@Override
	public String toString() {
		return dataElement;
	}

}
