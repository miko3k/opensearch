package org.deletethis.search.parser.opensearch;

import java.nio.charset.Charset;

class Evaluation {
    private final String searchTerms, suggestionPrefix, suggestionIndex;
    private final Charset inputEncoding, outputEncoding;

    public Evaluation(String searchTerms, Charset inputEncoding, Charset outputEncoding, String suggestionPrefix, String suggestionIndex) {
        this.searchTerms = searchTerms;
        this.inputEncoding = inputEncoding;
        this.outputEncoding = outputEncoding;
        this.suggestionPrefix = suggestionPrefix;
        this.suggestionIndex = suggestionIndex;
    }

    public String getSearchTerms() {
        return searchTerms;
    }

    public Charset getInputEncoding() {
        return inputEncoding;
    }

    public Charset getOutputEncoding() {
        return outputEncoding;
    }

    public String getSuggestionPrefix() {
        return suggestionPrefix;
    }

    public String getSuggestionIndex() {
        return suggestionIndex;
    }
}
