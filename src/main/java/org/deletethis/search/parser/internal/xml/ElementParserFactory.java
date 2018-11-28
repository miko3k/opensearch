package org.deletethis.search.parser.internal.xml;

import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.SearchPlugin;

public interface ElementParserFactory<T extends ElementParser> {
    T createElementParser();
    SearchPlugin createPlugin(T elementParser, byte[] originalSource) throws PluginParseException;
}
