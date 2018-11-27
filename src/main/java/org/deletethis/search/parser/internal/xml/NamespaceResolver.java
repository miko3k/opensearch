package org.deletethis.search.parser.internal.xml;

/** Wrapper around {@link org.xml.sax.helpers.NamespaceSupport} to improve readability and testability */
public interface NamespaceResolver {
    String getURI(String prefix);
}
