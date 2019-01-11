package org.deletethis.search.parser;

import java.util.Objects;

public class SearchQuery {
    private String value;
    private Suggestion suggestion;

    private SearchQuery(String value, Suggestion suggestion) {
        this.value = value;
        this.suggestion = suggestion;
    }

    public static SearchQuery of(String hello) {
        return new SearchQuery(Objects.requireNonNull(hello), null);
    }

    public static SearchQuery of(Suggestion hello) {
        return new SearchQuery(null, Objects.requireNonNull(hello));
    }

    public String getValue() {
        return value;
    }

    public Suggestion getSuggestion() {
        return suggestion;
    }

    public String getAnyValue() {
        if(value == null) {
            return getSuggestion().getValue();
        } else {
            return value;
        }
    }

    @Override
    public String toString() {
        if(value != null) {
            return '"' + value + '"';
        } else {
            return "[" + suggestion.getValue() + "]";
        }
    }
}
