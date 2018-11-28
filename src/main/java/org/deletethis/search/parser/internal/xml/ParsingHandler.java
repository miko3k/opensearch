package org.deletethis.search.parser.internal.xml;


import org.deletethis.search.parser.PluginParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.NamespaceSupport;

import java.util.ArrayDeque;
import java.util.Deque;

class ParsingHandler extends DefaultHandler {
    private NamespaceSupport namespaces;
    private boolean needNewContext = true;
    private Deque<ElementParser> elementParserStack;
    private NamespaceResolver nsr = new NamespaceResolver() {
        @Override
        public String getURI(String prefix) {
            return namespaces.getURI(prefix);
        }
    };

    public ParsingHandler(ElementParser initialElementParser) {
        this.elementParserStack = new ArrayDeque<>(10);
        this.elementParserStack.addLast(initialElementParser);
    }

    @Override
    public void startDocument()  {
        namespaces = new NamespaceSupport();
    }

    @Override
    public void endDocument() {
        if(elementParserStack.size() != 1) {
            throw new AssertionError();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        ElementParser last = this.elementParserStack.getLast();
        last.text(ch, start, length);
    }

    public void startPrefixMapping(String prefix, String uri)
             {

        if (needNewContext) {
            namespaces.pushContext();
            needNewContext = false;
        }
        namespaces.declarePrefix(prefix, uri);

    }

    public void endPrefixMapping(String prefix){

        namespaces.popContext();
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (needNewContext) namespaces.pushContext();

        AttributeResolver asr = new AttributeResolver() {
            @Override
            public String getValue(String namespace, String name) {
                return attributes.getValue(namespace, name);
            }

            @Override
            public String getValue(String name) {
                return attributes.getValue(name);
            }
        };

        ElementParser elementParser;
        try {
            elementParser = elementParserStack.getLast().startElement(uri, localName, asr, nsr);
        } catch (PluginParseException e) {
            throw new SAXException(e);
        }
        elementParserStack.addLast(elementParser);

        needNewContext = true;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            elementParserStack.removeLast().endElement();
        } catch (PluginParseException e) {
            throw new SAXException(e);
        }
        super.endElement(uri, localName, qName);
    }

}
