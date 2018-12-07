package org.deletethis.search.parser.internal.xml;

import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.ErrorCode;
import org.deletethis.search.parser.SearchPlugin;
import org.deletethis.search.parser.SearchPluginDeserializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

public class XmlSearchPluginDeserializer implements SearchPluginDeserializer {
    private final List<ElementParserFactory<?>> list;

    public XmlSearchPluginDeserializer(ElementParserFactory<?> ... elementParserFactories) {
        this.list = Arrays.asList(elementParserFactories);
    }

    private static void parse(byte[] is, ElementParser initialElementParser) throws PluginParseException {
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
            if(cause instanceof PluginParseException) {
                PluginParseException c = (PluginParseException) cause;
                throw new PluginParseException(c.getErrorCode(), c.getMessage(), e);
            } else {
                throw new PluginParseException(ErrorCode.NOT_WELL_FORMED, e);
            }
        } catch (ParserConfigurationException | IOException e) {
            // parser should be configured correctly and we are reading byte array,
            // therefore IOException should not occur
            throw new IllegalStateException(e);
        }
    }

    @Override
    final public SearchPlugin loadSearchPlugin(byte[] bytes) throws PluginParseException {
        Objects.requireNonNull(bytes, "Input stream is null");

        RootParser rootParser = new RootParser(list);
        parse(bytes, rootParser);
        return rootParser.createPlugin(bytes);
    }
}
