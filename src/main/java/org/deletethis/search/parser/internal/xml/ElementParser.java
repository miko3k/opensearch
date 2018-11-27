package org.deletethis.search.parser.internal.xml;

import org.deletethis.search.parser.EngineParseException;

public interface ElementParser {
    ElementParser NOP = new ElementParser() {
        @Override
        public ElementParser startElement(String namespace, String localName, AttributeResolver attributes, NamespaceResolver namespaces) {
            return NOP;
        }
    };

    ElementParser startElement(String uri, String localName, AttributeResolver attributes, NamespaceResolver namespaces) throws EngineParseException;
    default void endElement() throws EngineParseException { }
    default void text(char[] ch, int start, int length) {}
}