package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.internal.xml.AttributeResolver;
import org.deletethis.search.parser.internal.xml.ElementParser;
import org.deletethis.search.parser.internal.xml.NamespaceResolver;
import org.deletethis.search.parser.util.UrlCharacters;

import java.nio.charset.StandardCharsets;

class SimpleUrlParser implements ElementParser {
    private final StringBuilder bld = new StringBuilder();
    private final TextConsumer consumer;

    public interface TextConsumer {
        void accept(String value) throws PluginParseException;
    }


    SimpleUrlParser(TextConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public ElementParser startElement(String uri, String localName, AttributeResolver attributes, NamespaceResolver namespaces) {
        return ElementParser.NOP;
    }

    @Override
    public void endElement() throws PluginParseException {
        consumer.accept(sanitize(bld.toString().trim()));

    }

    public static String sanitize(String str) {
        return UrlCharacters.encodeInvalid(str, StandardCharsets.UTF_8);
    }

    @Override
    public void text(char[] ch, int start, int length) {
        bld.append(ch, start, length);
    }
}
