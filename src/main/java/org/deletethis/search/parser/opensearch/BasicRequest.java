package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.Request;

import java.util.Collections;
import java.util.Map;

public class BasicRequest implements Request {
    private final String url;

    public BasicRequest(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getParametersEncoding() {
        return null;
    }

    @Override
    public Map<String, String> getParameters() {
        return Collections.emptyMap();
    }
}
