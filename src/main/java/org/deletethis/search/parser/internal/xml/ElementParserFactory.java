package org.deletethis.search.parser.internal.xml;

import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.SearchPlugin;

import java.util.Optional;

public interface ElementParserFactory<T extends ElementParser> {
    Optional<T> createElementParser(String namespace, String localName);
    SearchPlugin createPlugin(T elementParser, byte[] originalSource) throws PluginParseException;
}
