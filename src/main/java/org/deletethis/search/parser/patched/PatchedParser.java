package org.deletethis.search.parser.patched;

import org.deletethis.search.parser.EngineParseException;
import org.deletethis.search.parser.SearchEngine;
import org.deletethis.search.parser.PatchBuilder;
import org.deletethis.search.parser.internal.util.ByteArrays;
import org.deletethis.search.parser.internal.xml.AttributeResolver;
import org.deletethis.search.parser.internal.xml.ElementParser;
import org.deletethis.search.parser.internal.xml.NamespaceResolver;
import org.deletethis.search.parser.internal.xml.SearchEngineDeserializer;

public class PatchedParser implements ElementParser {
    private final SearchEngineDeserializer deserializer;
    private final PatchBuilder patch = new PatchBuilder();

    public PatchedParser(SearchEngineDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    private void setSource(String source) throws EngineParseException {
        byte[] bytes = ByteArrays.decodeBase64(source);
        patch.searchEngine(deserializer.deserialize(bytes));
    }

    @Override
    public ElementParser startElement(String namespace, String localName, AttributeResolver attributes, NamespaceResolver namespaces) throws EngineParseException {
        if(!namespace.equals(PatchedConstants.NAMESPACE))
            return NOP;

        switch (localName) {
            case PatchedConstants.SOURCE_ELEMENT: return new TextParser(this::setSource);
            case PatchedConstants.NAME_ELEMENT: return new TextParser(patch::name);
            case PatchedConstants.ATTR_ELEMENT: return new AttrParser(patch);
            default: return NOP;
        }
    }

    public SearchEngine toSearchEngine()  {
        return patch.build();
    }
}
