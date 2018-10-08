package org.deletethis.search.parser;

public class SuggestionParseError extends Exception {

    public SuggestionParseError(String message) {
        super(message);
    }

    public SuggestionParseError(String message, Throwable cause) {
        super(message, cause);
    }
}
