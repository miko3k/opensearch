package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.EngineParseException;
import org.deletethis.search.parser.ErrorCode;
import org.deletethis.search.parser.internal.xml.AttributeResolver;
import org.deletethis.search.parser.internal.xml.ElementParser;
import org.deletethis.search.parser.internal.xml.NamespaceResolver;

import javax.xml.namespace.QName;
import java.util.function.Consumer;
import java.util.function.Function;

class OpenSearchUrlParser implements ElementParser {

    private final Consumer<Template> urlConsumer;
    private final String template;
    private String indexOffset;
    private String pageOffset;
    private final NamespaceResolver namespaceResolver;
    private final Function<QName, Evaluable> paramResolver = new Function<QName, Evaluable>() {
        @Override
        public Evaluable apply(QName qName) {
            if(qName.getNamespaceURI().isEmpty() || qName.getNamespaceURI().equals(OpenSearchConstants.MAIN_NAMESPACE)) {
                switch (qName.getLocalPart()) {
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
            if(qName.getNamespaceURI().equals(OpenSearchConstants.PARAMETERS_NAMESPACE)) {
                switch (qName.getLocalPart()) {
                    case "suggestionPrefix": return EvaluationContext::getSuggestionPrefix;
                    case "suggestionIndex": return EvaluationContext::getSuggestionIndex;
                    default: return null;
                }
            }
            return null;
        }
    };

    OpenSearchUrlParser(AttributeResolver attributes, NamespaceResolver namespaces, Consumer<Template> urlConsumer) throws EngineParseException {
        this.urlConsumer = urlConsumer;
        this.namespaceResolver = namespaces;

        this.template = attributes.getValue("template");
        this.indexOffset = attributes.getValue("indexOffset");
        this.pageOffset = attributes.getValue("pageOffset");

        if(template == null) throw new EngineParseException(ErrorCode.BAD_SYNTAX, "Url without template");
        if(indexOffset == null) indexOffset = "1";
        if(pageOffset == null) pageOffset = "1";
    }

    @Override
    public void endElement() throws EngineParseException {
        urlConsumer.accept(new Template(template, namespaceResolver, paramResolver));
    }

    @Override
    public ElementParser startElement(String uri, String localName, AttributeResolver attributes, NamespaceResolver namespaces) {
        return NOP;
    }
}
