package org.deletethis.search.parser;

import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.SearchPlugin;

public interface SearchPluginDeserializer {
    SearchPlugin loadSearchPlugin(byte [] bytes) throws PluginParseException;
}
