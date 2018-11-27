package org.deletethis.search.parser.patched;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * There's no XML writer shared between Java and Android, let's not introduce external dependencies for such a trivial task
 */
class PoorXmlWriter {
    private final String prefix, namespace;
    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream(2048);
    private final OutputStreamWriter writer = new OutputStreamWriter(bytes, StandardCharsets.UTF_8);

    public PoorXmlWriter(String prefix, String namespace) {
        this.prefix = prefix;
        this.namespace = namespace;
    }

    public void writeRoot(String element) {
        try {
            writer.write("<?xml version=\"1.1\" encoding=\"UTF-8\"?>\n");
            writer.write("<" + prefix + ":" + element + " xmlns:" + prefix + "=\"" + namespace + "\">");
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private void xmlEscapeText(Writer sb, String t) throws IOException {
        for(int i = 0; i < t.length(); i++){
            char c = t.charAt(i);
            switch(c){
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '\"': sb.append("&quot;"); break;
                case '&': sb.append("&amp;"); break;
                case '\'': sb.append("&apos;"); break;
                default: sb.append(c); break;
            }
        }
    }

    public void endRoot(String element) {
        try {
            writer.write("</" + prefix + ":" + element + ">");
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public void writeTextElement(String element, String body) {
        try {
            writer.write("<" + prefix + ":" + element + ">");
            xmlEscapeText(writer, body);
            writer.write("</" + prefix + ":" + element + ">");
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public byte [] toByteArray() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return bytes.toByteArray();
    }
}
