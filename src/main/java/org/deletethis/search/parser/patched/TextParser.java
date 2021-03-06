package org.deletethis.search.parser.patched;

import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.internal.xml.AttributeResolver;
import org.deletethis.search.parser.internal.xml.ElementParser;
import org.deletethis.search.parser.internal.xml.NamespaceResolver;

class TextParser implements ElementParser {
    private final StringBuilder bld = new StringBuilder();
    private final TextConsumer consumer;

    public interface TextConsumer {
        void accept(String value) throws PluginParseException;
    }


    TextParser(TextConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public ElementParser startElement(String uri, String localName, AttributeResolver attributes, NamespaceResolver namespaces) {
        return ElementParser.NOP;
    }

    @Override
    public void endElement() throws PluginParseException {
        consumer.accept(bld.toString());

    }

    @Override
    public void text(char[] ch, int start, int length) {
        bld.append(ch, start, length);
    }
}
