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
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
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

    private void xmlEscapeText(Writer sb, String t) throws IOException {
        for(int i = 0; i < t.length(); i++){
            char c = t.charAt(i);
            switch(c){
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '\"': sb.append("&quot;"); break;
                case '&': sb.append("&amp;"); break;
                case '\'': sb.append("&apos;"); break;
                default: sb.append(c); break;
            }
        }
    }

    @Override
    public byte[] serialize() {
        ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
        try(OutputStreamWriter bld = new OutputStreamWriter(out)) {

            // there's no XML writer which works both on Android and Java without external dependencies.
            // And this XML is very simple!

            bld.write("<?xml version=\"1.1\" encoding=\"UTF-8\"?>\n");
            bld.write("<" + NS_PREFIX + ":" + PatchedConstants.ROOT_ELEMENT + " xmlns:" + NS_PREFIX + "=\"" + PatchedConstants.NAMESPACE + "\">");
            if (name != null) {
                bld.write("<" + NS_PREFIX + ":" + PatchedConstants.NAME_ELEMENT + ">");
                xmlEscapeText(bld, name);
                bld.write("</" + NS_PREFIX + ":" + PatchedConstants.NAME_ELEMENT + ">");
            }

            bld.write("<" + NS_PREFIX + ":" + PatchedConstants.SOURCE_ELEMENT + ">");
            String serialized = Base64.getEncoder().encodeToString(searchEngine.serialize());
            xmlEscapeText(bld, serialized);
            bld.write("</" + NS_PREFIX + ":" + PatchedConstants.SOURCE_ELEMENT + ">");

            bld.write("</" + NS_PREFIX + ":" + PatchedConstants.ROOT_ELEMENT + ">");
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        return out.toByteArray();
    }

    @Override
    public SearchEnginePatch patch() {
        SearchEnginePatch patch = searchEngine.patch();
        return patch.name(name);
    }
}
