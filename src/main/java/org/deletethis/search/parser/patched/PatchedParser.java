package org.deletethis.search.parser.patched;

import org.deletethis.search.parser.EngineParseException;
import org.deletethis.search.parser.SearchEngine;
import org.deletethis.search.parser.SearchEnginePatch;
import org.deletethis.search.parser.internal.text.TextEncoding;
import org.deletethis.search.parser.internal.xml.AttributeResolver;
import org.deletethis.search.parser.internal.xml.ElementParser;
import org.deletethis.search.parser.internal.xml.NamespaceResolver;
import org.deletethis.search.parser.internal.xml.SearchEngineDeserializer;

public class PatchedParser implements ElementParser {
    private final SearchEngineDeserializer deserializer;
    private final SearchEnginePatch patch = new SearchEnginePatch();

    public PatchedParser(SearchEngineDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    private void setSource(String source) throws EngineParseException {
        byte[] bytes = TextEncoding.decodeBase64(source);
        patch.searchEngine(deserializer.deserialize(bytes));
    }

    @Override
    public ElementParser startElement(String namespace, String localName, AttributeResolver attributes, NamespaceResolver namespaces) throws EngineParseException {
        if(!namespace.equals(PatchedConstants.NAMESPACE))
            return NOP;

        switch (localName) {
            case PatchedConstants.SOURCE_ELEMENT: return new TextParser(this::setSource);
            case PatchedConstants.NAME_ELEMENT: return new TextParser(patch::name);
            default: return NOP;
        }
    }

    public SearchEngine toSearchEngine()  {
        return patch.createSearchEngine();
    }
}
