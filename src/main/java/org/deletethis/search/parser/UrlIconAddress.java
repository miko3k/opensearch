package org.deletethis.search.parser;

import java.util.*;

final public class UrlIconAddress extends AbstractList<String> implements IconAddress  {
    private List<String> urls;

    public static final UrlIconAddress NONE = new UrlIconAddress(Collections.emptyList());

    private UrlIconAddress(List<String> urls) {
        this.urls = urls;
    }

    public static UrlIconAddress of(List<String> urls) {
        if(urls.isEmpty()) {
            return NONE;
        } else {
            return new UrlIconAddress(new ArrayList<>(urls));
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
        if (!(o instanceof UrlIconAddress)) return false;
        UrlIconAddress strings = (UrlIconAddress) o;
        return urls.equals(strings.urls);
    }

    @Override
    public int hashCode() {
        return urls.hashCode();
    }
}
