package org.deletethis.search.parser.xml;

import org.deletethis.search.parser.EngineParseException;
import org.deletethis.search.parser.ErrorCode;
import org.deletethis.search.parser.SearchEngine;

import javax.xml.namespace.QName;
import java.util.Map;

class RootParser implements ElementParser {
    private final Map<QName, SearchElementParser> theMap;
    private SearchElementParser selectedParser;

    RootParser(Map<QName, SearchElementParser> theMap) {
        this.theMap = theMap;
    }

    @Override
    public ElementParser startElement(String namespace, String localName, AttributeResolver attributes, NamespaceResolver namespaces) throws EngineParseException {
        SearchElementParser searchElementParser = theMap.get(new QName(namespace, localName));
        if(searchElementParser == null) {
            throw new EngineParseException(ErrorCode.BAD_SYNTAX, "Root element is {" + namespace + "}" + localName);
        }
        selectedParser = searchElementParser;
        return searchElementParser;
    }

    /**
     * @param source May or may not be used
     */
    SearchEngine getSearchEngine(byte [] source) throws EngineParseException {
        return selectedParser.toSearchEngine(source);
    }
}
