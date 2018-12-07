package org.deletethis.search.parser.patched;

import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.SearchPlugin;
import org.deletethis.search.parser.PatchBuilder;
import org.deletethis.search.parser.SearchPluginIcon;
import org.deletethis.search.parser.internal.util.ByteArrays;
import org.deletethis.search.parser.internal.xml.AttributeResolver;
import org.deletethis.search.parser.internal.xml.ElementParser;
import org.deletethis.search.parser.internal.xml.NamespaceResolver;
import org.deletethis.search.parser.SearchPluginDeserializer;

import java.util.ArrayList;
import java.util.List;

public class PatchedParser implements ElementParser {
    private final SearchPluginDeserializer deserializer;
    private final PatchBuilder patch = new PatchBuilder();

    public PatchedParser(SearchPluginDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    private void setSource(String source) throws PluginParseException {
        byte[] bytes = ByteArrays.decodeBase64(source);
        patch.plugin(deserializer.loadSearchPlugin(bytes));
    }

    @Override
    public ElementParser startElement(String namespace, String localName, AttributeResolver attributes, NamespaceResolver namespaces) throws PluginParseException {
        if(!namespace.equals(PatchedConstants.NAMESPACE))
            return NOP;

        switch (localName) {
            case PatchedConstants.SOURCE_ELEMENT: return new TextParser(this::setSource);
            case PatchedConstants.NAME_ELEMENT: return new TextParser(patch::name);
            case PatchedConstants.IDENTIFIER_ELEMENT: return new TextParser(patch::identifier);
            case PatchedConstants.ATTR_ELEMENT: return new AttrParser(patch);
            case PatchedConstants.ICON_ELEMENT: return new ElementParser() {
                List<String> str = new ArrayList<>();

                @Override
                public ElementParser startElement(String uri, String localName, AttributeResolver attributes, NamespaceResolver namespaces) throws PluginParseException {
                    if(namespace.equals(PatchedConstants.NAMESPACE) && localName.equals(PatchedConstants.ICON_IMAGE_ELEMENT)) {
                        return new TextParser(s -> str.add(s));
                    } else {
                        return NOP;
                    }
                }

                @Override
                public void endElement() throws PluginParseException {
                    patch.icon(SearchPluginIcon.of(str));
                }
            };
            default: return NOP;
        }
    }

    public SearchPlugin createPlugin()  {
        return patch.build();
    }
}
