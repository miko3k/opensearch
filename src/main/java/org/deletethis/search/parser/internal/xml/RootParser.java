package org.deletethis.search.parser.internal.xml;

import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.ErrorCode;
import org.deletethis.search.parser.SearchPlugin;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RootParser implements ElementParser {
    private final List<ElementParserFactory<?>> theMap;

    private static class SelectedParser<T extends ElementParser> {
        T elementParser;
        ElementParserFactory<T> elementParserFactory;

        SelectedParser(ElementParserFactory<T> elementParserFactory,  T elementParser) {
            this.elementParserFactory = elementParserFactory;
            this.elementParser = elementParser;
        }

        SearchPlugin createPlugin(byte[] originalSource) throws PluginParseException {
            return elementParserFactory.createPlugin(elementParser, originalSource);
        }
    }

    private SelectedParser<?> selectedParser;

    RootParser(List<ElementParserFactory<?>> theMap) {
        this.theMap = theMap;
    }

    private <T extends ElementParser> ElementParser tryFactory(ElementParserFactory<T> elementParserFactory, String namespace, String localName) {
        Optional<T> elementParser = elementParserFactory.createElementParser(namespace, localName);
        if(elementParser.isPresent()) {
            selectedParser = new SelectedParser<>(elementParserFactory, elementParser.get());
            return selectedParser.elementParser;
        } else {
            return null;
        }
    }

    @Override
    public ElementParser startElement(String namespace, String localName, AttributeResolver attributes, NamespaceResolver namespaces) throws PluginParseException {
        for(ElementParserFactory<?> elementParserFactory: theMap) {
            ElementParser elementParser = tryFactory(elementParserFactory, namespace, localName);
            if(elementParser != null)
                return elementParser;
        }

        throw new PluginParseException(ErrorCode.BAD_SYNTAX, "Root element is {" + namespace + "}" + localName);
    }

    /**
     * @param source May or may not be used
     */
    public SearchPlugin createPlugin(byte [] source) throws PluginParseException {
        return selectedParser.createPlugin(source);
    }
}
