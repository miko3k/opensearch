package org.deletethis.search.parser.patched;

import org.deletethis.search.parser.*;
import org.deletethis.search.parser.internal.text.TextEncoding;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public class PatchedSearchEngine implements SearchEngine {
    private final SearchEngine searchEngine;
    private final String name;
    private String checksum;
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
        PoorXmlWriter writer = new PoorXmlWriter(NS_PREFIX, PatchedConstants.NAMESPACE);
        writer.writeRoot(PatchedConstants.ROOT_ELEMENT);
        if (name != null) {
            writer.writeTextElement(PatchedConstants.NAME_ELEMENT, name);
        }
        String serialized = TextEncoding.encodeBase64(searchEngine.serialize());
        writer.writeTextElement(PatchedConstants.SOURCE_ELEMENT, serialized);
        writer.endRoot(PatchedConstants.ROOT_ELEMENT);
        return writer.toByteArray();
    }

    @Override
    public SearchEnginePatch patch() {
        SearchEnginePatch patch = searchEngine.patch();
        return patch.name(name);
    }

    @Override
    public String getChecksum() {
        // synchronization not needed, reference writes are atomic
        if(checksum == null) {
            StringBuilder bld = new StringBuilder(1024);
            bld.append(searchEngine.getChecksum());
            bld.append("NAME-").append(name);
            byte[] bytes = bld.toString().getBytes(StandardCharsets.UTF_8);
            this.checksum = TextEncoding.sha1Sum(bytes);
        }
        return checksum;
    }
}
