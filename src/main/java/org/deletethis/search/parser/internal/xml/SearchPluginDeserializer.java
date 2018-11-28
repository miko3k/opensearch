package org.deletethis.search.parser.internal.xml;

import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.SearchPlugin;

public interface SearchPluginDeserializer {
    SearchPlugin deserialize(byte [] bytes) throws PluginParseException;
}
