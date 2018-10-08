package org.deletethis.search.parser.opensearch;

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
    private static void parse(InputStream is, ElementParser initialElementParser) throws IOException {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser saxParser = null;
            saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(new MyParser(initialElementParser));
            xmlReader.parse(new InputSource(is));
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }


    public static SearchEngine loadOpenSearch(InputStream is) throws IOException {
        Objects.requireNonNull(is, "Input stream is null");

        RootParser rootParser = new RootParser();
        parse(is, rootParser);
        return rootParser.getOpenSearch();
    }
}
