package no.statnett.larm.edifact;

public class EdifactParserException extends RuntimeException {

    private static final long serialVersionUID = -8743452980063980177L;

    EdifactParserException(String message) {
        super(message);
    }

    EdifactParserException(String positionInfo, String message) {
        super(message + ": " + positionInfo);
    }

    EdifactParserException(String positionInfo, String message, Throwable t) {
        super(message + ": " + positionInfo, t);
    }

}
