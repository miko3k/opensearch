package org.deletethis.search.parser.opensearch;

interface Evaluable {
    String evaluate(Evaluation evaluation);

    static Evaluable of(String str) {
        return evaluation -> str;
    }
}
