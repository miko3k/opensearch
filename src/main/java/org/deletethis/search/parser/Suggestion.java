package org.deletethis.search.parser;

import java.util.Optional;

public interface Suggestion {
    String getValue();
    Optional<String> getUrl();
    Optional<String> getDescription();
}
