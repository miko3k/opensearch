package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.PluginParseException;

public interface TemplateParamResolver {
    Evaluable getParameterValue(String namespacePrefix, String localPart) throws PluginParseException;
}
