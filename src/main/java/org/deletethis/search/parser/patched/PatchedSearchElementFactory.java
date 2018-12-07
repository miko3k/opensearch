package org.deletethis.search.parser.patched;

import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.SearchPlugin;
import org.deletethis.search.parser.SearchPluginDeserializer;
import org.deletethis.search.parser.internal.xml.ElementParserFactory;

import java.util.Optional;

public class PatchedSearchElementFactory implements ElementParserFactory<PatchedParser> {
    private final SearchPluginDeserializer rootDeserializer;

    public PatchedSearchElementFactory(SearchPluginDeserializer rootDeserializer) {
        this.rootDeserializer = rootDeserializer;
    }

    @Override
    public Optional<PatchedParser> createElementParser(String namespace, String localName) {
        if(!PatchedConstants.NAMESPACE.equals(namespace)) return Optional.empty();
        if(!PatchedConstants.ROOT_ELEMENT.equals(localName)) return Optional.empty();

        return Optional.of(new PatchedParser(rootDeserializer));
    }

    @Override
    public SearchPlugin createPlugin(PatchedParser elementParser, byte[] originalSource) throws PluginParseException {
        return elementParser.createPlugin();
    }
}
