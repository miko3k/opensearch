package org.deletethis.search.parser;

import org.deletethis.search.parser.patched.PatchedSearchEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PatchBuilder {
    private SearchEngine searchEngine;
    private String name;
    private Map<String, String> custom = new HashMap<>();

    public PatchBuilder() {

    }

    public PatchBuilder searchEngine(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
        return this;
    }

    public String getName() {
        return name;
    }

    public PatchBuilder name(String name) {
        this.name = name;
        return this;
    }

    public PatchBuilder attr(String name, String value) {
        custom.put(Objects.requireNonNull(name), Objects.requireNonNull(value));
        return this;
    }

    public PatchBuilder removeAttr(String name) {
        custom.remove(name);
        return this;
    }

    public boolean isSomethingPatched() {
        return name != null || !custom.isEmpty();
    }

    public PatchBuilder clear() {
        custom.clear();
        name(null);
        return this;
    }

    public SearchEngine build() {
        if(searchEngine == null)
            throw new IllegalStateException("no search engine set");

        if(isSomethingPatched()) {
            return new PatchedSearchEngine(searchEngine, name, custom);
        } else {
            return searchEngine;
        }
    }
}
