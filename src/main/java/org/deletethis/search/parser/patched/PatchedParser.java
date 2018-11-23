package org.deletethis.search.parser.patched;

import org.deletethis.search.parser.EngineParseException;
import org.deletethis.search.parser.SearchEngine;
import org.deletethis.search.parser.SearchEnginePatch;
import org.deletethis.search.parser.xml.AttributeResolver;
import org.deletethis.search.parser.xml.ElementParser;
import org.deletethis.search.parser.xml.NamespaceResolver;
import org.deletethis.search.parser.xml.SearchEngineDeserializer;

import java.util.Base64;

public class PatchedParser implements ElementParser {
    private final SearchEngineDeserializer deserializer;
    private final SearchEnginePatch patch = new SearchEnginePatch();

    public PatchedParser(SearchEngineDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    private void setSource(String source) throws EngineParseException {
        byte[] bytes = Base64.getDecoder().decode(source);
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
