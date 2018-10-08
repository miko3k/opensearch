package org.deletethis.search.parser.opensearch;

public class OpenSearchParseError extends RuntimeException {
    public OpenSearchParseError() {
    }

    public OpenSearchParseError(String message) {
        super(message);
    }

    public OpenSearchParseError(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenSearchParseError(Throwable cause) {
        super(cause);
    }

    public OpenSearchParseError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
