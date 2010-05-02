package no.statnett.larm.edifact;

public class EdifactParserException extends RuntimeException {

	private static final long serialVersionUID = -8743452980063980177L;

	EdifactParserException(String message) {
		super(message);
	}

	EdifactParserException(ParserContext ctx, String message) {
		super(message + ": " + ctx.formatPosition());
	}

	EdifactParserException(String message, Throwable t) {
		super(message, t);
	}

	EdifactParserException(ParserContext ctx, Throwable t) {
		super(t.getMessage() + ": parsing at " + ctx.formatPosition(), t);
	}
}
