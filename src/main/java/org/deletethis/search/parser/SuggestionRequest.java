package org.deletethis.search.parser;

import java.util.List;

public interface SuggestionRequest {
    String getUri();
    List<Suggestion> parseResult(String body) throws SuggestionParseError;
}
