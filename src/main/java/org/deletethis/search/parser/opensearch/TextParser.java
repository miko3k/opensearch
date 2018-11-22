package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.EngineParseException;

class TextParser implements ElementParser {
    private final StringBuilder bld = new StringBuilder();
    private final TextConsumer consumer;

    public interface TextConsumer {
        void accept(String value) throws EngineParseException;
    }


    public TextParser(TextConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public ElementParser startElement(String uri, String localName, AttributeResolver attributes, NamespaceResolver namespaces) {
        return ElementParser.NOP;
    }

    @Override
    public void endElement() throws EngineParseException {
        consumer.accept(bld.toString().trim().replaceAll("\\s+", " "));

    }

    @Override
    public void text(char[] ch, int start, int length) {
        bld.append(ch, start, length);
    }
}
