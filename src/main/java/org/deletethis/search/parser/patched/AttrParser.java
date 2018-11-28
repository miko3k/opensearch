package org.deletethis.search.parser.patched;

import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.PatchBuilder;
import org.deletethis.search.parser.internal.xml.AttributeResolver;
import org.deletethis.search.parser.internal.xml.ElementParser;
import org.deletethis.search.parser.internal.xml.NamespaceResolver;

public class AttrParser implements ElementParser {
    private final PatchBuilder resultConsumer;

    public AttrParser(PatchBuilder resultConsumer) {
        this.resultConsumer = resultConsumer;
    }

    private String name = null;
    private String value = null;

    @Override
    public ElementParser startElement(String namespace, String localName, AttributeResolver attributes, NamespaceResolver namespaces) throws PluginParseException {
        if(!namespace.equals(PatchedConstants.NAMESPACE))
            return NOP;

        switch (localName) {
            case PatchedConstants.ATTR_NAME_ELEMENT: return new TextParser(v -> name = v);
            case PatchedConstants.ATTR_VALUE_ELEMENT: return new TextParser(v -> value = v);
            default: return NOP;
        }
    }

    @Override
    public void endElement() throws PluginParseException {
        if(name != null && value != null)
            resultConsumer.attr(name, value);
    }
}
