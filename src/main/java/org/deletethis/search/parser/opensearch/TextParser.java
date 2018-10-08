package org.deletethis.search.parser.opensearch;

import java.util.function.Consumer;

public class TextParser implements ElementParser {
    private final StringBuilder bld = new StringBuilder();
    private final Consumer<String> consumer;

    public TextParser(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    @Override
    public ElementParser startElement(String uri, String localName, AttributeResolver attributes, NamespaceResolver namespaces) {
        return ElementParser.NOP;
    }

    @Override
    public void endElement() throws OpenSearchParseError {
        consumer.accept(bld.toString());

    }

    @Override
    public void text(char[] ch, int start, int length) {
        bld.append(ch, start, length);
    }
}
