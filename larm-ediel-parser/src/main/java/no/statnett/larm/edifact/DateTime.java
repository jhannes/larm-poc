package no.statnett.larm.edifact;

public class DateTime {

	public static DateTime createDateTime(EdifactDataElement dataElement, String format) {
		if (dataElement == null)
			return null;
		return new DateTime(dataElement.toString(), format);
	}

	private final String format;

	private final String strDateTime;

	public DateTime(String strDateTime, String format) {
		this.strDateTime = strDateTime;
		this.format = format;
	}

	public String getFormat() {
		return format;
	}

	public String getAsString() {
		return strDateTime;
	}
}
