package org.deletethis.search.parser.internal.xml;

import org.deletethis.search.parser.EngineParseException;
import org.deletethis.search.parser.SearchEngine;

public interface SearchEngineDeserializer {
    SearchEngine deserialize(byte [] bytes) throws EngineParseException;
}
