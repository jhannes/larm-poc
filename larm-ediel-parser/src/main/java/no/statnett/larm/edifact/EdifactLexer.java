package no.statnett.larm.edifact;

import java.io.IOException;
import java.io.PushbackReader;
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

    private static class CircularBuffer {
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
            int length = (int) Math.min(size, counter);
            char[] arr = new char[length];
            for (int i = 1; i <= length; i++) {
                arr[length - i] = buffer[(int) ((counter - i) % size)];
            }
            return new String(arr);
        }
    }

    private static final char NULL_CHAR = 0;
    private static final String SEGMENT_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SKIP_CHARS = "\t\r\n ";

    private final ParserContext ctx;
    private EdifactSegment currentSegment, pushedBackSegment;

    private boolean hasReadFirst;
    private final CircularBuffer lastRead;

    /* track position of reader */
    int lineNum, columnNum, segmentNum;

    private char prevChar = NULL_CHAR;

    private final PushbackReader reader;

    EdifactLexer(ParserContext ctx, Reader input, int lastReadBufferSize) {
        if (ctx == null) {
            throw new IllegalArgumentException("ctx cannot be null");
        } else if (null == input) {
            throw new IllegalArgumentException("input cannot be null");
        }
        this.reader = new PushbackReader(input);
        this.ctx = ctx;
        this.lastRead = new CircularBuffer(lastReadBufferSize);
    }

    EdifactLexer(ParserContext pc, String testString, int lastReadBufferSize) {
        this(pc, new StringReader(testString), lastReadBufferSize);
    }

    String formatPosition() {
        final String msg = "Reader: (line:%s, col:%s, segment:%s) last parsed: [%s].";
        return String.format(msg, lineNum, columnNum, segmentNum, lastRead());
    }

    List<String> getDataElementComponents(final CharSequence element) {
        return splitToStringList(element, ctx.componentDataElementSeparator, ctx.releaseIndicator);
    }

    List<String> getDataElements(final CharSequence segment) {
        return splitToStringList(segment, ctx.dataElementSeparator, ctx.releaseIndicator);
    }

    List<String> getDataElements(final EdifactSegment segment) {
        return splitToStringList(segment.segmentBody, ctx.dataElementSeparator, ctx.releaseIndicator);
    }

    EdifactSegment getSegment() {
        return this.currentSegment;
    }

    String getSegmentHeader(CharSequence segment) {
        return segment.subSequence(0, 3).toString();
    }

    private EdifactSegment initSegment() throws IOException {
        skipChars(SKIP_CHARS);

        if (isReaderEmpty()) {
            return null;
        }

        EdifactSegment segment = new EdifactSegment();

        segment.lineNum = lineNum;
        segment.segmentNum = ++segmentNum;
        segment.columnNum = columnNum;
        segment.ctx = ctx;

        String segmentName = read(3, SEGMENT_CHARACTERS);
        segment.setSegmentName(segmentName);
        return segment;
    }

    private boolean isReaderEmpty() throws IOException {
        int c = reader.read();
        if (c == -1) {
            return true;
        }
        reader.unread(c);
        return false;
    }

    String lastRead() {
        return lastRead.getContent();
    }

    private EdifactSegment popCurrent() {
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

    private String read(int characters, String validChars) throws IOException {
        StringBuilder result = new StringBuilder(characters);

        int c;
        while (result.length() < characters && ((c = readChar()) != -1)) {
            verifyValidChar(validChars, c);
            result.append((char) c);
        }

        verifyCharCount(characters, result);

        return result.toString();
    }

    int readChar() throws IOException {
        int c = reader.read();
        trackPosition(c);
        return c;
    }

    private EdifactSegment readFirstSegment() throws IOException {

        EdifactSegment segment = initSegment();
        if (segment == null)
            return null;

        if ("UNA".equals(segment.getSegmentName())) {
            segment.segmentBody = read(6, null);
            ctx.initSeparators(segment.segmentBody);
        } else {
            segment.segmentBody = readTo(ctx.segmentTerminator);
        }

        return segment;
    }

    private EdifactSegment readNextSegment() throws IOException {
        EdifactSegment segment = initSegment();
        if (segment == null)
            return null;

        skipChar(ctx.dataElementSeparator);

        segment.segmentBody = readTo(ctx.segmentTerminator);
        return segment;
    }

    EdifactSegment readSegment() throws IOException {
        if (popCurrent() == null) {
            EdifactSegment segment;
            if (!hasReadFirst) {
                hasReadFirst = true;
                segment = readFirstSegment();
            } else {
                segment = readNextSegment();
            }
            this.currentSegment = segment;
        }
        return this.currentSegment;
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

    private void skipChar(char charToSkip) throws IOException {
        int c;
        if ((c = reader.read()) != -1) {
            if (charToSkip != c) {
                reader.unread(c);
            } else {
                trackPosition(c);
            }
        }
    }

    private void skipChars(String charsToSkip) throws IOException {
        int c;
        while ((c = reader.read()) != -1) {
            if (charsToSkip.indexOf(c) == -1) {
                reader.unread(c);
                return;
            }
            trackPosition(c);
        }
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

    private void trackPosition(final int c) {
        columnNum++;

        if (prevChar == ctx.releaseIndicator) {
            // reset so escaping itself (e.g '??') won't escape next char
            prevChar = NULL_CHAR;
        } else if (c == '\n') {
            if (prevChar != '\r')
                lineNum++;
            columnNum = 0;
        } else if (c == '\r') {
            lineNum++;
            columnNum = 0;
        }

        prevChar = (char) c;
        lastRead.add(prevChar);
    }

    private void verifyCharCount(int characters, StringBuilder result) {
        if (result.length() < characters) {
            String s = "Wrong format, could only read [" + result.length() + "] characters, expected " + characters
                    + ", string read [" + result + "]";
            throw new EdifactParserException(formatPosition(), s);
        }
    }

    private void verifyValidChar(String validChars, int c) {
        if (validChars == null)
            return;

        if (validChars.indexOf(c) == -1) {
            String s = "Illegal character [" + (char) c + "]";
            throw new EdifactParserException(formatPosition(), s);
        }
    }
}
