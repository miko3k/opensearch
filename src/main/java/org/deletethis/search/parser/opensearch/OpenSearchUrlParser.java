package org.deletethis.search.parser.opensearch;

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
            if(qName.getNamespaceURI().isEmpty() || qName.getNamespaceURI().equals(Constants.MAIN_NAMESPACE)) {
                switch (qName.getLocalPart()) {
                    case "searchTerms": return Evaluation::getSearchTerms;
                    case "count": return Evaluable.of("50");
                    case "startIndex": return indexOffset == null ? null : Evaluable.of(indexOffset);
                    case "startPage": return pageOffset == null ? null : Evaluable.of(pageOffset);
                    case "language": return Evaluable.of("*");
                    case "inputEncoding": return (e) -> e.getInputEncoding().toString();
                    case "outputEncoding": return (e) -> e.getOutputEncoding().toString();
                    default: return null;
                }
            }
            if(qName.getNamespaceURI().equals(Constants.PARAMETERS_NAMESPACE)) {
                switch (qName.getLocalPart()) {
                    case "suggestionPrefix": return Evaluation::getSuggestionPrefix;
                    case "suggestionIndex": return Evaluation::getSuggestionIndex;
                    default: return null;
                }
            }
            return null;
        }
    };

    OpenSearchUrlParser(AttributeResolver attributes, NamespaceResolver namespaces, Consumer<Template> urlConsumer) throws OpenSearchParseError {
        this.urlConsumer = urlConsumer;
        this.namespaceResolver = namespaces;

        this.template = attributes.getValue("template");
        this.indexOffset = attributes.getValue("indexOffset");
        this.pageOffset = attributes.getValue("pageOffset");

        if(template == null) throw new OpenSearchParseError("Url without template");
        if(indexOffset == null) indexOffset = "1";
        if(pageOffset == null) pageOffset = "1";
    }

    @Override
    public void endElement() {
        urlConsumer.accept(new Template(template, namespaceResolver, paramResolver));
    }

    @Override
    public ElementParser startElement(String uri, String localName, AttributeResolver attributes, NamespaceResolver namespaces) {
        return NOP;
    }
}
