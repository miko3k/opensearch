package org.deletethis.search.parser.patched;

import org.deletethis.search.parser.*;
import org.deletethis.search.parser.internal.util.ByteArrays;
import org.deletethis.search.parser.internal.xml.PoorXmlWriter;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PatchedSearchEngine implements SearchEngine {
    private final SearchEngine searchEngine;
    private final String name;
    private String checksum;
    private final Map<String, String> attr;

    private final static String NS_PREFIX = "n";
    private final static Map<String, String> NSMAP = new HashMap<>();
    static {
        NSMAP.put(NS_PREFIX, PatchedConstants.NAMESPACE);
    }

    public PatchedSearchEngine(SearchEngine searchEngine, String name, Map<String, String> attr) {
        this.searchEngine = searchEngine;
        this.name = name;
        this.attr = Collections.unmodifiableMap(attr);
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
        PoorXmlWriter writer = new PoorXmlWriter();
        writer.startDocument();
        writer.startElement(NS_PREFIX, PatchedConstants.ROOT_ELEMENT, NSMAP);
        if (name != null) {
            writer.textElement(NS_PREFIX, PatchedConstants.NAME_ELEMENT, name);
        }
        for (Map.Entry<String, String> e : attr.entrySet()) {
            writer.startElement(NS_PREFIX, PatchedConstants.ATTR_ELEMENT);
            writer.textElement(NS_PREFIX, PatchedConstants.ATTR_NAME_ELEMENT, e.getKey());
            writer.textElement(NS_PREFIX, PatchedConstants.ATTR_VALUE_ELEMENT, e.getValue());
            writer.endElement();
        }
        String serialized = ByteArrays.encodeBase64(searchEngine.serialize());
        writer.textElement(NS_PREFIX, PatchedConstants.SOURCE_ELEMENT, serialized);
        writer.endElement();
        writer.endDocument();
        return writer.toByteArray();
    }

    @Override
    public PatchBuilder patch() {
        PatchBuilder patch = searchEngine.patch();
        return patch.name(name);
    }

    @Override
    public String getChecksum() {
        // synchronization not needed, reference writes are atomic
        if(checksum == null) {
            StringBuilder bld = new StringBuilder(1024);
            bld.append(searchEngine.getChecksum());
            bld.append("-NAME-").append(name);
            for (Map.Entry<String, String> e : attr.entrySet()) {
                bld.append("-A-NAME-").append(e.getKey());
                bld.append("-A-VALUE-").append(e.getValue());
            }
            byte[] bytes = bld.toString().getBytes(StandardCharsets.UTF_8);
            this.checksum = ByteArrays.sha1Sum(bytes);
        }
        return checksum;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attr;
    }
}
