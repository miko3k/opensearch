package org.deletethis.search.parser;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SearchEngine {
    String getName();
    String getSearchUrl(SearchQuery search);
    Optional<String> getUpdateUrl();
    List<String> getIconUrls();
    boolean supportsSuggestions();
    SuggestionRequest getSuggestions(SearchQuery search);
    Map<Property, Value> getProperties();
}
