package no.statnett.larm.edifact;


/**
 * Track context while parsing message, including delimiters, position in
 * message.
 */
class ParserContext {

    char componentDataElementSeparator = ':';
    char dataElementSeparator = '+';
    char decimalSeparator = '.';
    char releaseIndicator = '?';
    char segmentTerminator = '\'';

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
}
