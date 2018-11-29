package org.deletethis.search.parser;

import org.deletethis.search.parser.opensearch.OpenSearchConstants;
import org.deletethis.search.parser.opensearch.OpenSearchParser;
import org.deletethis.search.parser.opensearch.OpenSearchPlugin;
import org.deletethis.search.parser.patched.PatchedConstants;
import org.deletethis.search.parser.patched.PatchedParser;
import org.deletethis.search.parser.internal.xml.ParserImpl;
import org.deletethis.search.parser.internal.xml.ElementParserFactory;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class SearchPluginFactory {
    private ParserImpl GLOBAL_PARSER;

    public SearchPluginFactory() {
        GLOBAL_PARSER = new ParserImpl();
        GLOBAL_PARSER.add(new QName(OpenSearchConstants.MAIN_NAMESPACE, "OpenSearchDescription"), new ElementParserFactory<OpenSearchParser>() {
            @Override
            public OpenSearchParser createElementParser() {
                return new OpenSearchParser();
            }
            @Override
            public SearchPlugin createPlugin(OpenSearchParser elementParser, byte[] originalSource) throws PluginParseException {
                return new OpenSearchPlugin(elementParser, originalSource);
            }
        });
        GLOBAL_PARSER.add(new QName(PatchedConstants.NAMESPACE, PatchedConstants.ROOT_ELEMENT), new ElementParserFactory<PatchedParser>() {
            @Override
            public PatchedParser createElementParser() {
                return new PatchedParser(GLOBAL_PARSER);
            }

            @Override
            public SearchPlugin createPlugin(PatchedParser elementParser, byte[] originalSource) throws PluginParseException {
                return elementParser.createPlugin();
            }
        });
    }

    private byte[] readFully(InputStream inputStream) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }

    public SearchPlugin loadSearchPlugin(byte [] bytes) throws PluginParseException {
        Objects.requireNonNull(bytes, "Input array is null");

        return GLOBAL_PARSER.deserialize(bytes);
    }

    public SearchPlugin loadSearchPlugin(InputStream is) throws IOException, PluginParseException {
        // this class should autodetect file format and dispatch to appropriate parser... however,
        // because we support only one format, things are rather simple

        Objects.requireNonNull(is, "Input stream is null");
        byte []data = readFully(is);

        return loadSearchPlugin(data);
    }
}
