package org.deletethis.search.parser.internal.xml;

/** Wrapper around {@link org.xml.sax.Attributes} to improve testability and readability */
public interface AttributeResolver {
    String getValue(String namespace, String name);

    String getValue(String name);
}
