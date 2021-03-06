package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.ErrorCode;
import org.deletethis.search.parser.internal.xml.AttributeResolver;
import org.deletethis.search.parser.internal.xml.ElementParser;
import org.deletethis.search.parser.internal.xml.NamespaceResolver;

import java.util.function.Consumer;

class OpenSearchUrlParser implements ElementParser {

    private final Consumer<Template> urlConsumer;
    private final String template;
    private String indexOffset;
    private String pageOffset;
    private final NamespaceResolver namespaceResolver;

    private final TemplateParamResolver paramResolver = new TemplateParamResolver() {
        private String resolveNs(String prefix) throws PluginParseException {
            if(prefix != null) {
                String ns = namespaceResolver.getURI(prefix);

                if(ns == null) {
                    throw new PluginParseException(ErrorCode.BAD_SYNTAX, "No namespace is registered with prefix: " + prefix);
                }
                return ns;
            } else {
                return null;
            }
        }

        @Override
        public Evaluable getParameterValue(String prefix, String localPart) throws PluginParseException {
            String ns = resolveNs(prefix);

            if(ns == null || ns.equals(OpenSearchConstants.MAIN_NAMESPACE)) {
                switch (localPart) {
                    case "searchTerms": return EvaluationContext::getSearchTerms;
                    case "count": return Evaluable.of("50");
                    case "startIndex": return indexOffset == null ? null : Evaluable.of(indexOffset);
                    case "startPage": return pageOffset == null ? null : Evaluable.of(pageOffset);
                    case "language": return Evaluable.of("*");
                    case "inputEncoding": return (e) -> e.getInputEncoding().toString();
                    case "outputEncoding": return (e) -> e.getOutputEncoding().toString();
                    default: return null;
                }
            }
            if(ns.equals(OpenSearchConstants.PARAMETERS_NAMESPACE)) {
                switch (localPart) {
                    case "suggestionPrefix": return EvaluationContext::getSuggestionPrefix;
                    case "suggestionIndex": return EvaluationContext::getSuggestionIndex;
                    default: return null;
                }
            }
            return null;
        }
    };

    OpenSearchUrlParser(AttributeResolver attributes, NamespaceResolver namespaces, Consumer<Template> urlConsumer) throws PluginParseException {
        this.urlConsumer = urlConsumer;
        this.namespaceResolver = namespaces;

        this.template = attributes.getValue("template");
        this.indexOffset = attributes.getValue("indexOffset");
        this.pageOffset = attributes.getValue("pageOffset");

        if(template == null) throw new PluginParseException(ErrorCode.BAD_SYNTAX, "Url without template");
        if(indexOffset == null) indexOffset = "1";
        if(pageOffset == null) pageOffset = "1";
    }

    @Override
    public void endElement() throws PluginParseException {
        urlConsumer.accept(new Template(template, paramResolver));
    }

    @Override
    public ElementParser startElement(String uri, String localName, AttributeResolver attributes, NamespaceResolver namespaces) {
        return NOP;
    }
}
