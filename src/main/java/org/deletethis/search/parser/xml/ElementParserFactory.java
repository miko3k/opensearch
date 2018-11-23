package org.deletethis.search.parser.xml;

import org.deletethis.search.parser.EngineParseException;
import org.deletethis.search.parser.SearchEngine;

public interface ElementParserFactory<T extends ElementParser> {
    T createElementParser();
    SearchEngine toSearchEngine(T elementParser, byte[] originalSource) throws EngineParseException;
}
