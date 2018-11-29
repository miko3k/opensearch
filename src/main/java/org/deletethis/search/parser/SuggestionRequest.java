package org.deletethis.search.parser;

import java.util.List;

public interface SuggestionRequest extends Request {
    List<Suggestion> parseResult(String body) throws SuggestionParseException;
}
