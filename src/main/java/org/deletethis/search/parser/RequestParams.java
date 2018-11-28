package org.deletethis.search.parser;

import java.util.Map;

public interface RequestParams {
    String getUrl();

    String getParametersEncoding();

    Map<String, String> getParameters();
}
