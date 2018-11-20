package org.deletethis.search.parser.opensearch;

import java.nio.charset.Charset;

class EvaluationContext {
    private final String searchTerms, suggestionPrefix, suggestionIndex;
    private final Charset inputEncoding, outputEncoding;

    EvaluationContext(String searchTerms, Charset inputEncoding, Charset outputEncoding, String suggestionPrefix, String suggestionIndex) {
        this.searchTerms = searchTerms;
        this.inputEncoding = inputEncoding;
        this.outputEncoding = outputEncoding;
        this.suggestionPrefix = suggestionPrefix;
        this.suggestionIndex = suggestionIndex;
    }

    String getSearchTerms() {
        return searchTerms;
    }

    Charset getInputEncoding() {
        return inputEncoding;
    }

    Charset getOutputEncoding() {
        return outputEncoding;
    }

    String getSuggestionPrefix() {
        return suggestionPrefix;
    }

    String getSuggestionIndex() {
        return suggestionIndex;
    }
}
