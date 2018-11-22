package org.deletethis.search.parser.xml;

/** Wrapper around {@link org.xml.sax.helpers.NamespaceSupport} to improve readability and testability */
public interface NamespaceResolver {
    String getURI(String prefix);
}
