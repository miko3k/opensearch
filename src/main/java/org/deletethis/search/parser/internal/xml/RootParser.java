package org.deletethis.search.parser.internal.xml;

import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.ErrorCode;
import org.deletethis.search.parser.SearchPlugin;

import javax.xml.namespace.QName;
import java.util.Map;

class RootParser implements ElementParser {
    private final Map<QName, ElementParserFactory<?>> theMap;

    private static class SelectedParser<T extends ElementParser> {
        ElementParserFactory<T> elementParserFactory;
        T elementParser;

        SelectedParser(ElementParserFactory<T> elementParserFactory) {
            this.elementParserFactory = elementParserFactory;
            this.elementParser = elementParserFactory.createElementParser();
        }

        SearchPlugin createPlugin(byte[] originalSource) throws PluginParseException {
            return elementParserFactory.createPlugin(elementParser, originalSource);
        }
    }

    private SelectedParser<?> selectedParser;

    RootParser(Map<QName, ElementParserFactory<?>> theMap) {
        this.theMap = theMap;
    }

    @Override
    public ElementParser startElement(String namespace, String localName, AttributeResolver attributes, NamespaceResolver namespaces) throws PluginParseException {
        ElementParserFactory<?> elementParserFactory = theMap.get(new QName(namespace, localName));
        if(elementParserFactory == null) {
            throw new PluginParseException(ErrorCode.BAD_SYNTAX, "Root element is {" + namespace + "}" + localName);
        }
        selectedParser = new SelectedParser<>(elementParserFactory);
        return selectedParser.elementParser;
    }

    /**
     * @param source May or may not be used
     */
    SearchPlugin createPlugin(byte [] source) throws PluginParseException {
        return selectedParser.createPlugin(source);
    }
}
