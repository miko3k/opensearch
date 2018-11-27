package org.deletethis.search.parser;

import java.util.Map;
import java.util.Optional;

public interface SearchEngine {
    String getName();
    String getSearchUrl(SearchQuery search);
    Optional<String> getUpdateUrl();
    Optional<AddressList> getIconAddress();
    boolean supportsSuggestions();
    SuggestionRequest getSuggestions(SearchQuery search);
    Map<PropertyName, PropertyValue> getProperties();
    String getIdentifier();
    String getChecksum();
    byte [] serialize();
    SearchEnginePatch patch();
}
