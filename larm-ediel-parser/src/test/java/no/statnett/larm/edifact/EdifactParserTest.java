package no.statnett.larm.edifact;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;
import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.fest.assertions.Fail;
import org.junit.Test;

public class EdifactParserTest {

	@Test
	public void shouldSplitIntoSegments() throws Exception {
		String edifactFile = // "UNA:+.? '\n" +
				"UNB+UNOC:glarb'" +
				"UNH+1+QUOTES:D:96A:UN:EDIEL2+S'" +
				"CUX+2:NOK'NAD+FR+7080005053136::9+++++++NO'LIN+1++1608:::SM'\n" +
				"UNT+169+1'\n" +
				"UNZ+1+gabba'";
		EdifactParser parser = new EdifactParser(new StringReader(edifactFile));
		assertThat(segmentNames(parser))
			.containsExactly("UNB", "UNH", "CUX", "NAD", "LIN", "UNT", "UNZ");
	}

	@Test
	public void shouldReadServiceStringAdvice() throws Exception {
		String edifactFile = "UNA:+.? '\n" +
			"UNB+UNOC:glarb'" +
			"UNH+1+GABBA'" +
			"UNT+169+1'\n" +
			"UNZ+1+gabba'";

		EdifactParser parser = new EdifactParser(new StringReader(edifactFile));
		assertThat(segmentNames(parser))
			.containsExactly("UNA", "UNB", "UNH", "UNT", "UNZ");
	}

	@Test
	public void shouldReadServiceStringAdviceChars() throws Exception {
		String edifactFile = "UNA|,*/ ^\n" +
			"UNB,UNOC|glarb^" +
			"UNH,1,GABBA^" +
			"UNT,169,1^\n" +
			"UNZ,1,gabba^";

		EdifactParser parser = new EdifactParser(new StringReader(edifactFile));
		assertThat(segmentNames(parser))
			.containsExactly("UNA", "UNB", "UNH", "UNT", "UNZ");
	}

	@Test
	public void shouldReadDataElements() throws Exception {
		EdifactParser parser = new EdifactParser(new StringReader(""));
		EdifactSegment segment = parser.parseSegment("IFT+3+NO MORE FLIGHTS");
		assertThat(segment.getSegmentName()).isEqualTo("IFT");
		assertThat(collect(segment.getDataElements(), on(EdifactSegment.class).toString()))
			.containsExactly("3", "NO MORE FLIGHTS");
	}

	@Test
	public void shouldReadComponentData() throws Exception {
		EdifactParser parser = new EdifactParser(new StringReader(""));
		EdifactSegment segment = parser.parseSegment("PDI++C:3+Y::3+F::1");
		assertThat(segment.getSegmentName()).isEqualTo("PDI");
		assertThat(segment.getDataElements().get(0).getComponentData()).isEmpty();
		assertThat(segment.getDataElements().get(1).getComponentData()).containsExactly("C", "3");
		assertThat(segment.getDataElements().get(3).getComponentData()).containsExactly("F", "", "1");
	}

	@Segment("PDI")
	public static class PdiEdifactSegment extends QualifiedEdifactSegment {
		@Override
		public String getQualifier() {
			return getDataElements().get(0).toString();
		}
	}

	@Test
	public void shouldReadOptionalSegmentBySegmentName() throws Exception {
		String edifactFile =
			"UNB+UNOC:glarb'" +
			"UNH+1+GABBA'" +
			"PDI++C:3+Y::3+F::1'";
		EdifactParser parser = new EdifactParser(new StringReader(edifactFile));

		assertThat(parser.readEdifactSegment().getSegmentName()).isEqualTo("UNB");
		assertThat(parser.readOptionalSegment(PdiEdifactSegment.class)).isNull();
		assertThat(parser.readEdifactSegment().getSegmentName()).isEqualTo("UNH");

		PdiEdifactSegment segment = parser.readOptionalSegment(PdiEdifactSegment.class);
		assertThat(segment.getDataElements()).hasSize(4);
	}

	@Test
	public void shouldReadOptionalSegmentByNameAndQualifier() throws Exception {
		String edifactFile = "UNB+UNOC:glarb'" + "UNH+1+GABBA'" + "PDI+FOO+C:3+Y::3+F::1'";
		EdifactParser parser = new EdifactParser(new StringReader(edifactFile));
		assertThat(parser.readEdifactSegment().getSegmentName()).isEqualTo("UNB");
		assertThat(parser.readOptionalSegment(PdiEdifactSegment.class, "BAR")).isNull();
		assertThat(parser.readEdifactSegment().getSegmentName()).isEqualTo("UNH");

		assertThat(parser.readOptionalSegment(PdiEdifactSegment.class, "BAR")).isNull();
		assertThat(parser.readOptionalSegment(PdiEdifactSegment.class, "FOO")).isNotNull();
	}

	@Test
	public void shouldPushBack() throws Exception {
		String edifactFile = "UNB+UNOC:glarb'" + "UNH+1+GABBA'" + "PDI++C:3+Y::3+F::1'";
		EdifactParser parser = new EdifactParser(new StringReader(edifactFile));

		assertThat(parser.readEdifactSegment().getSegmentName()).isEqualTo("UNB");
		assertThat(parser.readEdifactSegment().getSegmentName()).isEqualTo("UNH");
		parser.pushBack();
		assertThat(parser.readEdifactSegment().getSegmentName()).isEqualTo("UNH");
		assertThat(parser.readEdifactSegment().getSegmentName()).isEqualTo("PDI");
	}

	@Test
	public void shouldReadOptionalStuff() throws Exception {
		String edifactFile = "UNA:+.? '\n" + "UNB+UNOC:glarb'" + "UNH+1+GABBA'" + "PDI++C:3+Y::3+F::1'" + "UNT+169+1'\n"
				+ "UNZ+1+gabba'";
		SegmentSource parser = new EdifactParser(new StringReader(edifactFile));

		assertThat(parser.readOptionalSegment("UNA").getSegmentName()).isEqualTo("UNA");
		assertThat(parser.readMandatorySegment("UNB")).isNotNull();
		assertThat(parser.readMandatorySegment("UNH")).isNotNull();

		assertThat(parser.readOptionalSegment("IFT")).isNull();
		assertThat(parser.readOptionalSegment("PDI")).isNotNull();
	}

	@Test
	public void shouldThrowWhenRequiredSegmentIsMissing() throws Exception {
		String edifactFile = "UNH+1+GABBA'";
		EdifactParser parser = new EdifactParser(new StringReader(edifactFile));

		try {
			parser.readMandatorySegment("UNB");
			Fail.fail();
		} catch (Exception e) {
			assertThat(e.getMessage()).contains("UNB").contains("UNH");
		}

		assertThat(parser.readEdifactSegment().getSegmentName()).isEqualTo("UNH");
	}

	private List<String> segmentNames(EdifactParser parser) throws IOException {
		return collect(parser.eachSegment(), on(EdifactSegment.class).getSegmentName());
	}

}
