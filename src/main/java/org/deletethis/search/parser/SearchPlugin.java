package org.deletethis.search.parser;

import java.util.Map;
import java.util.Optional;

public interface SearchPlugin {
    String getName();
    HttpMethod getSearchMethod();
    Request getSearchRequest(SearchQuery search);
    Optional<String> getUpdateUrl();
    SearchPluginIcon getIcon();
    boolean supportsSuggestions();
    HttpMethod getSuggestionMethod();
    SuggestionRequest getSuggestionRequest(SearchQuery search);
    Map<PropertyName, PropertyValue> getProperties();
    String getIdentifier();

    byte [] serialize();
    PatchBuilder patch();
    Map<String, String> getAttributes();
}
