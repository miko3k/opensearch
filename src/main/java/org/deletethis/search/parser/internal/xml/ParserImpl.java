package org.deletethis.search.parser.internal.xml;

import org.deletethis.search.parser.EngineParseException;
import org.deletethis.search.parser.ErrorCode;
import org.deletethis.search.parser.SearchEngine;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ParserImpl implements SearchEngineDeserializer{
    private final Map<QName, ElementParserFactory<?>> theMap = new HashMap<>();

    public void add(QName name, ElementParserFactory<?> parser) {
        this.theMap.put(name, parser);

    }

    private static void parse(byte[] is, ElementParser initialElementParser) throws EngineParseException {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(new ParsingHandler(initialElementParser));
            xmlReader.parse(new InputSource(new ByteArrayInputStream(is)));
        } catch (SAXException e) {
            // NOT getCause, it returns null on Android!!
            Exception cause = e.getException();
            if(cause instanceof EngineParseException) {
                EngineParseException c = (EngineParseException) cause;
                throw new EngineParseException(c.getErrorCode(), c.getMessage(), e);
            } else {
                throw new EngineParseException(ErrorCode.NOT_WELL_FORMED, e);
            }
        } catch (ParserConfigurationException | IOException e) {
            // parser should be configured correctly and we are reading byte array,
            // therefore IOException should not occur
            throw new IllegalStateException(e);
        }
    }

    @Override
    public SearchEngine deserialize(byte[] bytes) throws EngineParseException {
        Objects.requireNonNull(bytes, "Input stream is null");

        RootParser rootParser = new RootParser(theMap);
        parse(bytes, rootParser);
        return rootParser.getSearchEngine(bytes);
    }
}
