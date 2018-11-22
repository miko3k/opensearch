package org.deletethis.search.parser;

import org.deletethis.search.parser.opensearch.OpenSearchConstants;
import org.deletethis.search.parser.opensearch.OpenSearchParser;
import org.deletethis.search.parser.patched.PatchedConstants;
import org.deletethis.search.parser.patched.PatchedParser;
import org.deletethis.search.parser.xml.ParserImpl;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class SearchEngineFactory {
    private static ParserImpl GLOBAL_PARSER;
    static {
        GLOBAL_PARSER = new ParserImpl();
        GLOBAL_PARSER.add(new QName(OpenSearchConstants.MAIN_NAMESPACE, "OpenSearchDescription"), new OpenSearchParser());
        GLOBAL_PARSER.add(new QName(PatchedConstants.NAMESPACE, PatchedConstants.ROOT_ELEMENT), new PatchedParser(GLOBAL_PARSER));
    }

    private SearchEngineFactory() { }

    private static byte[] readFully(InputStream inputStream) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }

    public static SearchEngine loadSearchEngine(byte [] bytes) throws EngineParseException {
        Objects.requireNonNull(bytes, "Input array is null");

        return GLOBAL_PARSER.deserialize(bytes);
    }

    public static SearchEngine loadSearchEngine(InputStream is) throws IOException, EngineParseException {
        // this class should autodetect file format and dispatch to appropriate parser... however,
        // because we support only one format, things are rather simple

        Objects.requireNonNull(is, "Input stream is null");
        byte []data = readFully(is);

        return loadSearchEngine(data);
    }
}
