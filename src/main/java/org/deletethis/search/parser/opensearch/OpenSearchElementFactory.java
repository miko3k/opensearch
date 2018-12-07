package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.SearchPlugin;
import org.deletethis.search.parser.internal.xml.ElementParserFactory;

import java.util.Optional;

public class OpenSearchElementFactory implements ElementParserFactory<OpenSearchParser> {
    @Override
    public Optional<OpenSearchParser> createElementParser(String namespace, String localName) {
        if(!OpenSearchConstants.MAIN_NAMESPACE.equals(namespace)) return Optional.empty();
        if(!"OpenSearchDescription".equals(localName)) return Optional.empty();

        return Optional.of(new OpenSearchParser());
    }

    @Override
    public SearchPlugin createPlugin(OpenSearchParser elementParser, byte[] originalSource) throws PluginParseException {
        return new OpenSearchPlugin(elementParser, originalSource);
    }
}
