package org.deletethis.search.parser.opensearch;

/** Wrapper around {@link org.xml.sax.helpers.NamespaceSupport} to improve testability */
interface NamespaceResolver {
    String getURI(String prefix);
}
