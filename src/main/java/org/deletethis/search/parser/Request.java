package org.deletethis.search.parser;

import java.util.Map;

public interface Request {
    String FORM_URLENCODED = "application/x-www-form-urlencoded";
    String FORM_DATA = "multipart/form-data";

    String getUrl();

    /** only for POST requests, may return null for GET */
    String getParametersEncoding();

    /** only for POST requests, returns an empty map for GET */
    Map<String, String> getParameters();
}
