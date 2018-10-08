package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.Suggestion;

import java.util.Objects;
import java.util.Optional;

public class OpenSearchSuggestion implements Suggestion {

    private final String value;
    private final String url;
    private final String description;
    private final String suggestionPrefix;
    private final String suggestionIndex;

    public OpenSearchSuggestion(String value, String url, String description, String suggestionPrefix, String suggestionIndex) {
        this.value = Objects.requireNonNull(value);
        this.url = url;
        this.description = description;
        this.suggestionPrefix = Objects.requireNonNull(suggestionPrefix);
        this.suggestionIndex = Objects.requireNonNull(suggestionIndex);
    }

    @Override
    public String getValue() {
        return value;
    }

    String getSuggestionPrefix() {
        return suggestionPrefix;
    }

    String getSuggestionIndex() {
        return suggestionIndex;
    }

    @Override
    public Optional<String> getUrl() {
        return Optional.ofNullable(url);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    @Override
    public String toString() {
        return "OpenSearchSuggestion{" +
                "value='" + value + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", suggestionPrefix='" + suggestionPrefix + '\'' +
                ", suggestionIndex='" + suggestionIndex + '\'' +
                '}';
    }
}
