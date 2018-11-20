package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.EngineParseException;
import org.deletethis.search.parser.ErrorCode;
import org.deletethis.search.parser.SearchEngine;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class OpenSearchFactory {
    private static void parse(InputStream is, ElementParser initialElementParser) throws IOException, EngineParseException {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(new ParsingHandler(initialElementParser));
            xmlReader.parse(new InputSource(is));
        } catch (SAXException e) {
            Throwable cause = e.getCause();
            if(cause instanceof EngineParseException) {
                throw (EngineParseException)cause;
            } else {
                throw new EngineParseException(ErrorCode.NOT_WELL_FORMED, e);
            }
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }


    public static SearchEngine loadOpenSearch(InputStream is) throws IOException, EngineParseException {
        Objects.requireNonNull(is, "Input stream is null");

        RootParser rootParser = new RootParser();
        parse(is, rootParser);
        return rootParser.getOpenSearch();
    }
}
