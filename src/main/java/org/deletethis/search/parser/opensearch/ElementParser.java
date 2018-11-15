package org.deletethis.search.parser.opensearch;

interface ElementParser {
    ElementParser NOP = new ElementParser() {
        @Override
        public ElementParser startElement(String namespace, String localName, AttributeResolver attributes, NamespaceResolver namespaces) {
            return NOP;
        }
    };

    ElementParser startElement(String uri, String localName, AttributeResolver attributes, NamespaceResolver namespaces) throws OpenSearchParseError;
    default void endElement() throws OpenSearchParseError { }
    default void text(char[] ch, int start, int length) {}
}
