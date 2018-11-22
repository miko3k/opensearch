package org.deletethis.search.parser;

import org.deletethis.search.parser.patched.PatchedSearchEngine;

public class SearchEnginePatch {
    private SearchEngine searchEngine;
    private String name;

    public SearchEnginePatch() {

    }

    public SearchEnginePatch searchEngine(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
        return this;
    }

    public String getName() {
        return name;
    }

    public SearchEnginePatch name(String name) {
        this.name = name;
        return this;
    }

    public boolean isSomethingPatched() {
        return name != null;
    }

    public SearchEnginePatch clear() {
        return name(null);
    }

    public SearchEngine createSearchEngine() {
        if(searchEngine == null)
            throw new IllegalStateException("no search engine set");

        if(isSomethingPatched()) {
            return new PatchedSearchEngine(searchEngine, name);
        } else {
            return searchEngine;
        }
    }
}
