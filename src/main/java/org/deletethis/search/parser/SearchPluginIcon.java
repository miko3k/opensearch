package org.deletethis.search.parser;

import java.util.*;

final public class SearchPluginIcon extends AbstractList<String> {
    private List<String> urls;

    public static final SearchPluginIcon NONE = new SearchPluginIcon(Collections.emptyList());

    private SearchPluginIcon(List<String> urls) {
        this.urls = urls;
    }

    public static SearchPluginIcon of(List<String> urls) {
        if(urls.isEmpty()) {
            return NONE;
        } else {
            return new SearchPluginIcon(new ArrayList<>(urls));
        }
    }

    @Override
    public String get(int index) {
        return urls.get(index);
    }

    @Override
    public int size() {
        return urls.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchPluginIcon)) return false;
        SearchPluginIcon strings = (SearchPluginIcon) o;
        return urls.equals(strings.urls);
    }

    @Override
    public int hashCode() {
        return urls.hashCode();
    }
}
