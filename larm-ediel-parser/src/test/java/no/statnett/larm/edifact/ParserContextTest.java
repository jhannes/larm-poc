package no.statnett.larm.edifact;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

public class ParserContextTest {

	@Test
	public void shouldTrackPositionAndLastRead() throws Exception {
		String testString = "UX'\t\nAB\r\n,1'X2345\n678912345";

		ParserContext pc = new ParserContext(15);
		StringReader r = new StringReader(testString);

		read(pc, r, 2);
		assertPosition(pc, 0, 2, 0);

		read(pc, r, 1);
		assertPosition(pc, 0, 3, 1);

		read(pc, r, 2);
		assertPosition(pc, 1, 0, 1);

		read(pc, r, 2);
		assertPosition(pc, 1, 2, 1);
		assertThat(pc.lastRead()).isEqualTo(testString.substring(0, 7));

		read(pc, r, 2);
		assertPosition(pc, 2, 1, 1);
		assertThat(pc.lastRead()).isEqualTo(testString.substring(0, 9));

		// read past cyclic buffer size
		read(pc, r, 12);
		assertPosition(pc, 3, 3, 2);

		assertThat(pc.lastRead()).isEqualTo(testString.substring(6, 21));

	}

	private void read(ParserContext pc, Reader r, int numChars)
			throws IOException {
		for (int i = 0; i < numChars; i++) {
			pc.readChar(r);
		}
	}

	private void assertPosition(ParserContext pc, int lineNum, int colNum,
			int segmentNum) throws Exception {
		assertThat(pc.lineNumber).isEqualTo(lineNum);
		assertThat(pc.columnNumber).isEqualTo(colNum);
		assertThat(pc.segmentNumber).isEqualTo(segmentNum);
	}
}
