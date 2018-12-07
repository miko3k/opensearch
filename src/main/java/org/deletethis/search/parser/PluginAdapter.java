package org.deletethis.search.parser;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class PluginAdapter implements SearchPlugin {
    protected final SearchPlugin target;

    public PluginAdapter(SearchPlugin target) {
        this.target = Objects.requireNonNull(target);
    }

    @Override
    public String getName() {
        return target.getName();
    }

    @Override
    public HttpMethod getSearchMethod() {
        return target.getSearchMethod();
    }

    @Override
    public Request getSearchRequest(SearchQuery search) {
        return target.getSearchRequest(search);
    }

    @Override
    public Optional<String> getUpdateUrl() {
        return target.getUpdateUrl();
    }

    @Override
    public SearchPluginIcon getIcon() {
        return target.getIcon();
    }

    @Override
    public boolean supportsSuggestions() {
        return target.supportsSuggestions();
    }

    @Override
    public HttpMethod getSuggestionMethod() {
        return target.getSuggestionMethod();
    }

    @Override
    public SuggestionRequest getSuggestionRequest(SearchQuery search) {
        return target.getSuggestionRequest(search);
    }

    @Override
    public Map<PropertyName, PropertyValue> getProperties() {
        return target.getProperties();
    }

    @Override
    public String getIdentifier() {
        return target.getIdentifier();
    }

    @Override
    public byte[] serialize() {
        return target.serialize();
    }

    @Override
    public PatchBuilder patch() {
        return target.patch();
    }

    @Override
    public Map<String, String> getAttributes() {
        return target.getAttributes();
    }
}
