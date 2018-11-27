package org.deletethis.search.parser.internal.xml;

import org.deletethis.search.parser.EngineParseException;
import org.deletethis.search.parser.SearchEngine;

public interface ElementParserFactory<T extends ElementParser> {
    T createElementParser();
    SearchEngine toSearchEngine(T elementParser, byte[] originalSource) throws EngineParseException;
}
