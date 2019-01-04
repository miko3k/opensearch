package org.deletethis.search.parser;

import org.deletethis.search.parser.patched.PatchedPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class PatchBuilder {
    private SearchPlugin searchPlugin;
    private String name;
    private Map<String, String> attr = new HashMap<>();
    private UrlIconAddress icon;
    private String identifier;

    public PatchBuilder() {

    }

    public PatchBuilder plugin(SearchPlugin searchPlugin) {
        this.searchPlugin = searchPlugin;
        return this;
    }

    public SearchPlugin getPlugin() {
        return searchPlugin;
    }


    public String getName() {
        return name;
    }

    public PatchBuilder name(String name) {
        this.name = name;
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public PatchBuilder identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }


    public PatchBuilder attr(String name, String value) {
        attr.put(Objects.requireNonNull(name), Objects.requireNonNull(value));
        return this;
    }

    public PatchBuilder icon(UrlIconAddress icon) {
        this.icon = icon;
        return this;
    }

    public UrlIconAddress getIcon() {
        return icon;
    }

    public PatchBuilder removeAttr(String name) {
        attr.remove(name);
        return this;
    }

    public boolean isSomethingPatched() {
        return name != null || !attr.isEmpty() || icon != null || identifier != null;
    }

    public PatchBuilder clear() {
        attr.clear();
        name(null);
        icon(null);
        identifier(null);
        return this;
    }

    public SearchPlugin build() {
        if(searchPlugin == null)
            throw new IllegalStateException("no search plugin set");

        if(isSomethingPatched()) {
            return new PatchedPlugin(this);
        } else {
            return searchPlugin;
        }
    }

    public Map<String, String> getAttributes() {
        return attr;
    }
}
