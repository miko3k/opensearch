package org.deletethis.search.parser.xml;

import org.deletethis.search.parser.EngineParseException;
import org.deletethis.search.parser.SearchEngine;

public interface SearchEngineDeserializer {
    SearchEngine deserialize(byte [] bytes) throws EngineParseException;
}
