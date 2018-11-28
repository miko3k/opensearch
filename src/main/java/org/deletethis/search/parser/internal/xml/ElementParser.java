package org.deletethis.search.parser.internal.xml;

import org.deletethis.search.parser.PluginParseException;

public interface ElementParser {
    ElementParser NOP = new ElementParser() {
        @Override
        public ElementParser startElement(String namespace, String localName, AttributeResolver attributes, NamespaceResolver namespaces) {
            return NOP;
        }
    };

    ElementParser startElement(String uri, String localName, AttributeResolver attributes, NamespaceResolver namespaces) throws PluginParseException;
    default void endElement() throws PluginParseException { }
    default void text(char[] ch, int start, int length) {}
}
