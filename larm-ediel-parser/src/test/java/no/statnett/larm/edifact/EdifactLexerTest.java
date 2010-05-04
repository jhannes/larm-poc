package no.statnett.larm.edifact;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class EdifactLexerTest {

	@Test
	public void shouldTrackPositionAndLastRead() throws Exception {
		String testString = "UX'\t\nAB\r\n,1'X2345\n678912345";

		ParserContext pc = new ParserContext();
		EdifactLexer lexer = new EdifactLexer(pc, testString, 15);

		read(lexer, 2);
		assertReaderPosition(lexer, 0, 2, 0);

		read(lexer, 1);
		assertReaderPosition(lexer, 0, 3, 0);

		read(lexer, 2);
		assertReaderPosition(lexer, 1, 0, 0);

		read(lexer, 2);
		assertReaderPosition(lexer, 1, 2, 0);
		assertThat(lexer.lastRead()).isEqualTo(testString.substring(0, 7));

		read(lexer, 2);
		assertReaderPosition(lexer, 2, 0, 0);
		assertThat(lexer.lastRead()).isEqualTo(testString.substring(0, 9));

		// read past cyclic buffer size
		read(lexer, 12);
		assertReaderPosition(lexer, 3, 3, 0);

		assertThat(lexer.lastRead()).isEqualTo(testString.substring(6, 21));

	}

	@Test
	public void shouldTrackPositionWhenPushBack() throws Exception {
		String testString = "UXA'\t\nABC\r\n,1'X2345\n678912345";

		ParserContext pc = new ParserContext();
		EdifactLexer lexer = new EdifactLexer(pc, testString, 15);

		EdifactSegment segment1 = lexer.readSegment();

		assertReaderPosition(lexer, 0, 4, 1);
		assertSegmentPosition(segment1, 0, 1);

		lexer.pushBack();
		EdifactSegment segment2 = lexer.readSegment();
		assertReaderPosition(lexer, 0, 4, 1);
		assertSegmentPosition(segment2, 0, 1);

		EdifactSegment segment3 = lexer.readSegment();
		assertReaderPosition(lexer, 2, 3, 2);
		assertSegmentPosition(segment3, 1, 2);

	}

	@Test
	public void shouldReadTokensWithEscapeCharacter() throws Exception {

		EdifactLexer lexer = new EdifactLexer(new ParserContext(), "", 15);

		List<String> tokens = lexer.splitToStringList(null, '+', '?');
		assertThat(tokens).isEmpty();

		tokens = lexer.splitToStringList("", '+', (char) 0);
		assertThat(tokens).isEmpty();

		tokens = lexer.splitToStringList("  ", '+', '?');
		assertThat(tokens).containsExactly("  ");

		tokens = lexer.splitToStringList("A++Å:?+?C", '+', '?');
		assertThat(tokens).containsExactly("A", "", "Å:+?C");

		tokens = lexer.splitToStringList("A++Å:?+?C+", '+', '?');
		assertThat(tokens).containsExactly("A", "", "Å:+?C", "");

		tokens = lexer.splitToStringList("A++B:?+C+", ':', '?');
		assertThat(tokens).containsExactly("A++B", "?+C+");

		tokens = lexer.splitToStringList("A++B:?+?C+", '+', (char) 0);
		assertThat(tokens).containsExactly("A", "", "B:?", "?C", "");

		tokens = lexer.splitToStringList("A++B:?+?C+", (char) 0, (char) 0);
		assertThat(tokens).containsExactly("A++B:?+?C+");

		tokens = lexer.splitToStringList("A++B:?+?C+", (char) 0, '?');
		assertThat(tokens).containsExactly("A++B:?+?C+");

	}

	private void read(EdifactLexer lexer, int numChars) throws IOException {
		for (int i = 0; i < numChars; i++) {
			lexer.readChar();
		}
	}

	private void assertReaderPosition(EdifactLexer lexer, int lineNum, int colNum, int segmentNum) throws Exception {
		assertThat(lexer.lineNum).isEqualTo(lineNum);
		assertThat(lexer.columnNum).isEqualTo(colNum);
		assertThat(lexer.segmentNum).isEqualTo(segmentNum);
	}

	private void assertSegmentPosition(EdifactSegment segment, int lineNum, int segmentNum) throws Exception {
		assertThat(segment.lineNum).isEqualTo(lineNum);
		assertThat(segment.segmentNum).isEqualTo(segmentNum);
	}
}
