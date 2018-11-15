package org.deletethis.search.parser.opensearch;

class JsonSuggestionParseError extends Exception {

    public JsonSuggestionParseError(String message) {
        super(message);
    }

    public JsonSuggestionParseError(String message, Throwable cause) {
        super(message, cause);
    }
}