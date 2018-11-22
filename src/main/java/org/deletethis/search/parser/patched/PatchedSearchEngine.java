package org.deletethis.search.parser.patched;

import org.deletethis.search.parser.AddressList;
import org.deletethis.search.parser.PropertyName;
import org.deletethis.search.parser.PropertyValue;
import org.deletethis.search.parser.SearchEngine;
import org.deletethis.search.parser.SearchEnginePatch;
import org.deletethis.search.parser.SearchQuery;
import org.deletethis.search.parser.SuggestionRequest;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

public class PatchedSearchEngine implements SearchEngine {
    private final SearchEngine searchEngine;
    private final String name;
    private final static String NS_PREFIX = "n";

    public PatchedSearchEngine(SearchEngine searchEngine, String name) {
        this.searchEngine = searchEngine;
        this.name = name;
    }

    @Override
    public String getName() {
        if(name != null) {
            return name;
        } else {
            return searchEngine.getName();
        }
    }

    @Override
    public String getSearchUrl(SearchQuery search) {
        return searchEngine.getSearchUrl(search);
    }

    @Override
    public Optional<String> getUpdateUrl() {
        return searchEngine.getUpdateUrl();
    }

    @Override
    public Optional<AddressList> getIconAddress() {
        return searchEngine.getIconAddress();
    }

    @Override
    public boolean supportsSuggestions() {
        return searchEngine.supportsSuggestions();
    }

    @Override
    public SuggestionRequest getSuggestions(SearchQuery search) {
        return searchEngine.getSuggestions(search);
    }

    @Override
    public Map<PropertyName, PropertyValue> getProperties() {
        return searchEngine.getProperties();
    }

    @Override
    public String getIdentifier() {
        return searchEngine.getIdentifier();
    }

    @Override
    public byte[] serialize() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(2048);
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
        try {
            XMLStreamWriter w = xmlOutputFactory.createXMLStreamWriter(outputStream);
            w.writeStartDocument();
            w.writeStartElement(NS_PREFIX, PatchedConstants.ROOT_ELEMENT, PatchedConstants.NAMESPACE);
            w.setPrefix(NS_PREFIX, PatchedConstants.NAMESPACE);
            w.writeNamespace(NS_PREFIX, PatchedConstants.NAMESPACE);

            if(name != null) {
                w.writeStartElement(PatchedConstants.NAMESPACE, PatchedConstants.NAME_ELEMENT);
                w.writeCharacters(name);
                w.writeEndElement();
            }

            byte[] data = searchEngine.serialize();
            w.writeStartElement(PatchedConstants.NAMESPACE, PatchedConstants.SOURCE_ELEMENT);
            String serialized = Base64.getEncoder().encodeToString(data);
            w.writeCharacters(serialized);
            w.writeEndElement();

            w.writeEndDocument();
            w.close();
        } catch (XMLStreamException e) {
            throw new IllegalStateException(e);
        }
        return outputStream.toByteArray();
    }

    @Override
    public SearchEnginePatch patch() {
        SearchEnginePatch patch = searchEngine.patch();
        return patch.name(name);
    }
}
