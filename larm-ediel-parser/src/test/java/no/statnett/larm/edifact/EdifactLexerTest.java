package no.statnett.larm.edifact;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class EdifactLexerTest {

	@Test
	public void shouldTrackPositionAndLastRead() throws Exception {
		String testString = "UX'\t\nAB\r\n,1'X2345\n678912345";

		ParserConfig pc = new ParserConfig();
		EdifactLexer lexer = new EdifactLexer(pc, testString, 15);

		read(lexer, 2);
		assertReaderPosition(lexer, 0, 2, 0);

		read(lexer, 1);
		assertReaderPosition(lexer, 0, 3, 1);

		read(lexer, 2);
		assertReaderPosition(lexer, 1, 0, 1);

		read(lexer, 2);
		assertReaderPosition(lexer, 1, 2, 1);
		assertThat(lexer.lastRead()).isEqualTo(testString.substring(0, 7));

		read(lexer, 2);
		assertReaderPosition(lexer, 2, 0, 1);
		assertThat(lexer.lastRead()).isEqualTo(testString.substring(0, 9));

		// read past cyclic buffer size
		read(lexer, 12);
		assertReaderPosition(lexer, 3, 3, 2);

		assertThat(lexer.lastRead()).isEqualTo(testString.substring(6, 21));

	}

	@Test
	public void shouldTrackPositionWhenPushBack() throws Exception {
		String testString = "UXA'\t\nABC\r\n,1'X2345\n678912345";

		ParserConfig pc = new ParserConfig();
		EdifactLexer lexer = new EdifactLexer(pc, testString, 15);

		lexer.readSegment();

		assertReaderPosition(lexer, 0, 4, 1);
		assertSegmentPosition(lexer, 0, 1);

		lexer.pushBack();
		lexer.readSegment();
		assertReaderPosition(lexer, 0, 4, 1);
		assertSegmentPosition(lexer, 0, 1);

		lexer.readSegment();
		assertReaderPosition(lexer, 2, 3, 2);
		assertSegmentPosition(lexer, 2, 2);

	}

	@Test
	public void shouldReadTokensWithEscapeCharacter() throws Exception {

		EdifactLexer lexer = new EdifactLexer(new ParserConfig(), "", 15);

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
		assertThat(lexer.getReaderLineNumber()).isEqualTo(lineNum);
		assertThat(lexer.getReaderColumnNumber()).isEqualTo(colNum);
		assertThat(lexer.getReaderSegmentNumber()).isEqualTo(segmentNum);
	}

	private void assertSegmentPosition(EdifactLexer lexer, int lineNum, int segmentNum) throws Exception {
		assertThat(lexer.getSegmentLineNumber()).isEqualTo(lineNum);
		assertThat(lexer.getSegmentNumber()).isEqualTo(segmentNum);
	}
}
