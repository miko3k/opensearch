package org.deletethis.search.parser.xml;

import org.deletethis.search.parser.EngineParseException;
import org.deletethis.search.parser.SearchEngine;

public interface SearchElementParser extends ElementParser {
    SearchEngine toSearchEngine(byte[] originalSource) throws EngineParseException;
}
