package org.deletethis.search.parser;

public class SuggestionParseException extends Exception {
    private static final long serialVersionUID = 1302133426211233367L;

    public SuggestionParseException(String message) {
        super(message);
    }

    public SuggestionParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
