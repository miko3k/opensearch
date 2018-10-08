package org.deletethis.search.parser.opensearch;

public class RootParser implements ElementParser {
    private OpenSearchParser theParser;

    @Override
    public ElementParser startElement(String namespace, String localName, AttributeResolver attributes, NamespaceResolver namespaces) {
        if(!namespace.equals(Constants.MAIN_NAMESPACE) || !localName.equals("OpenSearchDescription"))
            throw new OpenSearchParseError("Root element is {" + namespace + "}" + localName);

        theParser = new OpenSearchParser();
        return theParser;
    }

     OpenSearch getOpenSearch() {
        return theParser.getOpenSearch();
    }
}
