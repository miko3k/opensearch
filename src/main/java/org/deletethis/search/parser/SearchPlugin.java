package org.deletethis.search.parser;

import java.util.Map;
import java.util.Optional;

public interface SearchPlugin {
    String getName();
    String getSearchUrl(SearchQuery search);
    Optional<String> getUpdateUrl();
    SearchPluginIcon getIcon();
    boolean supportsSuggestions();
    SuggestionRequest getSuggestions(SearchQuery search);
    Map<PropertyName, PropertyValue> getProperties();
    String getIdentifier();

    byte [] serialize();
    PatchBuilder patch();
    Map<String, String> getAttributes();
}
