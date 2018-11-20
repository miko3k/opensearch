package org.deletethis.search.parser;

import org.deletethis.search.parser.opensearch.OpenSearchFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class SearchEngineFactory {
    private SearchEngineFactory() { }

    public static SearchEngine loadSearchEngine(byte [] data) throws EngineParseException {
        try {
            return loadSearchEngine(new ByteArrayInputStream(data));
        } catch (IOException e) {
            throw new IllegalStateException("ByteArrayInputStream should not cause IOException", e);
        }
    }

    public static SearchEngine loadSearchEngine(InputStream is) throws IOException, EngineParseException {
        // this class should autodetect file format and dispatch to appropriate parser... however,
        // because we support only one format, things are rather simple

        Objects.requireNonNull(is, "Input stream is null");
        return OpenSearchFactory.loadOpenSearch(is);
    }
}
