package org.deletethis.search.parser;

import java.util.Map;

public interface QueryUrl {
    HttpMethod getMethod();
    RequestParams getRequestParams(SearchQuery query);
}
