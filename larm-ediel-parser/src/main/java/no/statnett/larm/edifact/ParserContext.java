package no.statnett.larm.edifact;

import java.io.IOException;
import java.io.Reader;

/**
 * Track context while parsing message, including delimiters, position in
 * message.
 */
class ParserContext {
	private static final char NULL_CHAR = 0;

	char componentDataElementSeparator = ':';
	char dataElementSeparator = '+';
	char decimalSeparator = '.';
	char releaseIndicator = '?';
	char segmentTerminator = '\'';

	boolean hasReadFirst = false;

	/* cyclic buffer tracking last read characters. */
	private final char[] lastRead;
	private long lastReadCounter = -1;

	int lineNumber, columnNumber, segmentNumber;

	String currentSegment, pushedBackSegment;

	private char prev = NULL_CHAR;

	ParserContext(int lastReadBufferSize) {
		this.lastRead = new char[lastReadBufferSize];
	}

	String formatPosition() {
		final String msg = "(line:%s, col:%s, segment:%s) last parsed: [%s]";
		return String.format(msg, lineNumber, columnNumber, segmentNumber, lastRead());
	}

	void initSeparators(final String unaSegment) {
		if (unaSegment != null && unaSegment.length() > 5) {
			componentDataElementSeparator = unaSegment.charAt(0);
			dataElementSeparator = unaSegment.charAt(1);
			decimalSeparator = unaSegment.charAt(2);
			releaseIndicator = unaSegment.charAt(3);
			// 4 - reserved for future use
			segmentTerminator = unaSegment.charAt(5);
		}
	}

	String lastRead() {
		long charsRead = lastReadCounter + 1;
		int length = (int) Math.min((long) lastRead.length, charsRead);
		char[] lastReadChars = new char[length];
		for (int i = 1; i <= length; i++) {
			lastReadChars[length - i] = lastRead[(int) ((charsRead - i) % lastRead.length)];
		}
		return new String(lastReadChars);

	}

	char lastReadChar() {
		return prev;
	}

	String popCurrent() {
		if (pushedBackSegment != null) {
			currentSegment = pushedBackSegment;
			pushedBackSegment = null;
			return currentSegment;
		}
		return null;
	}

	void pushBack() {
		pushedBackSegment = currentSegment;
	}

	int readChar(final Reader reader) throws IOException {
		int c = reader.read();
		trackPosition(c);
		return c;
	}

	private void trackPosition(final int c) {
		columnNumber++;

		if (prev == releaseIndicator) {
			// reset so escaping itself (e.g '??') won't escape next char
			prev = NULL_CHAR;
		} else if (c == segmentTerminator) {
			segmentNumber++;
		} else if (c == '\n' && prev != '\r') {
			lineNumber++;
			columnNumber = 0;
		} else if (c == '\r') {
			lineNumber++;
			columnNumber = 0;
		}

		prev = (char) c;
		lastRead[(int) (++lastReadCounter % lastRead.length)] = prev;
	}
}
