package no.statnett.larm.edifact;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class reads the input and breaks it up into tokens (segments, data
 * elements). It also tracks the position it is at reading from the input, which
 * can then be used for error reporting.
 */
class EdifactLexer {

	static class CircularBuffer {
		private final char[] buffer;
		private long counter;
		private final int size;

		CircularBuffer(int size) {
			this.buffer = new char[size];
			this.size = size;
		}

		void add(final char c) {
			buffer[(int) (counter++ % size)] = c;
		}

		String getContent() {
			int length = (int) Math.min((long) size, counter);
			char[] arr = new char[length];
			for (int i = 1; i <= length; i++) {
				arr[length - i] = buffer[(int) ((counter - i) % size)];
			}
			return new String(arr);
		}
	}

	private static final int LINE = 0, COLUMN = 1, SEGMENT = 2;

	private static final char NULL_CHAR = 0;
	private static final int NUMPOSITIONS = 3;

	private final ParserConfig ctx;
	private boolean hasReadFirst = false;

	private char prevChar = NULL_CHAR;

	private final CircularBuffer lastRead;

	private final Reader reader;

	/* track position of pushed back segment. */
	private final int[] pushedBackSegmentPos = new int[NUMPOSITIONS];

	/* track position of reader */
	private final int[] readerPos = new int[NUMPOSITIONS];

	/* track position of current segment. */
	private final int[] segmentPos = new int[NUMPOSITIONS];

	private String segment, pushedBackSegment;

	EdifactLexer(ParserConfig ctx, Reader input, int lastReadBufferSize) {
		if (ctx == null) {
			throw new IllegalArgumentException("ctx cannot be null");
		} else if (null == input) {
			throw new IllegalArgumentException("input cannot be null");
		}
		this.reader = input;
		this.ctx = ctx;
		this.lastRead = new CircularBuffer(lastReadBufferSize);
	}

	EdifactLexer(ParserConfig pc, String testString, int lastReadBufferSize) throws IOException {
		this(pc, new StringReader(testString), lastReadBufferSize);
	}

	String formatPosition() {
		final String msg = "Segment start: (line:%s, segment:%s); " + //
				"Reader: (line:%s, col:%s, segment:%s) last parsed: [%s].";
		return String.format(msg, segmentPos[LINE], segmentPos[SEGMENT], //
				readerPos[LINE], readerPos[COLUMN], readerPos[SEGMENT], lastRead());
	}

	List<String> getDataElementComponents(final CharSequence element) {
		return splitToStringList(element, ctx.componentDataElementSeparator, ctx.releaseIndicator);
	}

	List<String> getDataElements(final CharSequence segment) {
		return splitToStringList(segment, ctx.dataElementSeparator, ctx.releaseIndicator);
	}

	int getReaderColumnNumber() {
		return readerPos[COLUMN];
	}

	int getReaderLineNumber() {
		return readerPos[LINE];
	}

	int getReaderSegmentNumber() {
		return readerPos[SEGMENT];
	}

	String getSegment() {
		return this.segment;
	}

	String getSegmentHeader(CharSequence segment) {
		return segment.subSequence(0, 3).toString();
	}

	int getSegmentLineNumber() {
		return segmentPos[LINE];
	}

	int getSegmentNumber() {
		return segmentPos[SEGMENT];
	}

	String lastRead() {
		return lastRead.getContent();
	}

	private String popCurrent() {
		if (pushedBackSegment != null) {
			segment = pushedBackSegment;
			pushedBackSegment = null;
			System.arraycopy(pushedBackSegmentPos, 0, segmentPos, 0, NUMPOSITIONS);
			return segment;
		}
		return null;
	}

	void pushBack() {
		pushedBackSegment = segment;
		System.arraycopy(segmentPos, 0, pushedBackSegmentPos, 0, NUMPOSITIONS);
	}

	private String read(int characters) throws IOException {
		StringBuilder result = new StringBuilder(characters);

		int c;
		while (result.length() < characters && ((c = readChar()) != -1)) {
			result.append((char) c);
		}
		return result.toString();
	}

	int readChar() throws IOException {
		int c = reader.read();
		trackPosition(c);
		return c;
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

	String readSegment() throws IOException {
		if (popCurrent() == null) {
			String str;
			if (!hasReadFirst) {
				hasReadFirst = true;
				str = readFirstSegment();
			} else {
				str = readTo(ctx.segmentTerminator);
			}

			this.segment = stripStart(str, " \n\t\r");
			System.arraycopy(readerPos, 0, segmentPos, 0, NUMPOSITIONS);
		}
		return this.segment;
	}

	private String readTo(char terminator) throws IOException {
		StringBuilder result = new StringBuilder();
		int c;

		while ((c = readChar()) != -1) {
			if (c == terminator)
				return result.toString();
			result.append((char) c);
		}
		return null;
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

	private void trackPosition(final int c) {
		readerPos[COLUMN]++;

		if (prevChar == ctx.releaseIndicator) {
			// reset so escaping itself (e.g '??') won't escape next char
			prevChar = NULL_CHAR;
		} else if (c == ctx.segmentTerminator) {
			readerPos[SEGMENT]++;
		} else if (c == '\n') {
			if (prevChar != '\r')
				readerPos[LINE]++;
			readerPos[COLUMN] = 0;
		} else if (c == '\r') {
			readerPos[LINE]++;
			readerPos[COLUMN] = 0;
		}

		prevChar = (char) c;
		lastRead.add(prevChar);
	}
}
