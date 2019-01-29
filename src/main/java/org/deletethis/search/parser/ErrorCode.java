package org.deletethis.search.parser;

public enum ErrorCode {
    NOT_WELL_FORMED,
    BAD_SYNTAX,
    USAGE_NOT_ALLOWED,
    NO_URL,
    INVALID_METHOD,
    /**
     * Currently not used, but will be kept, may be useful for subclasses to indicate
     * some application level problem
     */
    @SuppressWarnings("unused") INTERNAL_ERROR
}
