package org.deletethis.search.parser;

import org.deletethis.search.parser.internal.xml.XmlSearchPluginDeserializer;
import org.deletethis.search.parser.opensearch.OpenSearchElementFactory;
import org.deletethis.search.parser.patched.PatchedSearchElementFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class SearchPluginFactory implements SearchPluginDeserializer {
    private XmlSearchPluginDeserializer xmlSearchPluginDeserializer;

    public SearchPluginFactory() {
        xmlSearchPluginDeserializer = new XmlSearchPluginDeserializer(
                new OpenSearchElementFactory(),
                new PatchedSearchElementFactory(this)
        );
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

        return xmlSearchPluginDeserializer.loadSearchPlugin(bytes);
    }

    final public SearchPlugin loadSearchPlugin(InputStream is) throws IOException, PluginParseException {
        // this class should autodetect file format and dispatch to appropriate parser... however,
        // because we support only one format, things are rather simple

        Objects.requireNonNull(is, "Input stream is null");
        byte []data = readFully(is);

        return loadSearchPlugin(data);
    }
}
