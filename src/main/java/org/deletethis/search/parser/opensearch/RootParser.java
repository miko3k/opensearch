package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.EngineParseException;
import org.deletethis.search.parser.ErrorCode;

class RootParser implements ElementParser {
    private OpenSearchParser theParser;

    @Override
    public ElementParser startElement(String namespace, String localName, AttributeResolver attributes, NamespaceResolver namespaces) throws EngineParseException {
        if(!namespace.equals(Constants.MAIN_NAMESPACE) || !localName.equals("OpenSearchDescription"))
            throw new EngineParseException(ErrorCode.BAD_SYNTAX, "Root element is {" + namespace + "}" + localName);

        theParser = new OpenSearchParser();
        return theParser;
    }

     OpenSearch getOpenSearch() throws EngineParseException {
        return theParser.getOpenSearch();
    }
}
