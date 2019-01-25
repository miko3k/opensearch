package org.deletethis.search.parser.internal.xml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

/**
 * There's no XML writer shared between Java and Android, let's not introduce external dependencies
 */
public class PoorXmlWriter {
    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream(2048);
    private final Writer writer = new BufferedWriter(new OutputStreamWriter(bytes, StandardCharsets.UTF_8));
    private final Deque<Map.Entry<String, String>> stack = new ArrayDeque<>(10);

    public void startDocument() {
        try {
            writer.write("<?xml version=\"1.1\" encoding=\"UTF-8\"?>\n");
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public void startElement(String ns, String name, Map<String, String> nsmap) {
        try {
            writer.write("<");
            writer.write(ns);
            writer.write(":");
            writer.write(name);
            if(nsmap!=null) {
                for (Map.Entry<String, String> e : nsmap.entrySet()) {
                    writer.write(" xmlns:");
                    writer.write(e.getKey());
                    writer.write("=\"");
                    writer.write(e.getValue());
                    writer.write("\"");
                }
            }
            writer.write(">");
            stack.addLast(new AbstractMap.SimpleEntry<>(ns, name));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public void startElement(String ns, String name) {
        startElement(ns, name, null);
    }

    public void endElement() {
        try {
            Map.Entry<String, String> e = stack.removeLast();
            writer.write("</");
            writer.write(e.getKey());
            writer.write(":");
            writer.write(e.getValue());
            writer.write(">");
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public void text(String t) {
        try {
        for(int i = 0; i < t.length(); i++){
            char c = t.charAt(i);
            switch(c){
                case '<': writer.append("&lt;"); break;
                case '>': writer.append("&gt;"); break;
                case '\"': writer.append("&quot;"); break;
                case '&': writer.append("&amp;"); break;
                case '\'': writer.append("&apos;"); break;
                default: writer.append(c); break;
            }
        }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public void textElement(String ns, String element, String body) {
        startElement(ns, element);
        text(body);
        endElement();
    }

    public void endDocument() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public byte [] toByteArray() {
        return bytes.toByteArray();
    }
}
