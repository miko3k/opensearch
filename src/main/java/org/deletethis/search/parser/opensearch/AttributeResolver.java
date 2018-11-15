package org.deletethis.search.parser.opensearch;

/** Wrapper around {@link org.xml.sax.Attributes} to improve testability */
interface AttributeResolver {
    String getValue(String namespace, String name);

    String getValue(String name);
}
