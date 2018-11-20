package org.deletethis.search.parser.opensearch;

interface Evaluable {
    String evaluate(EvaluationContext evaluationContext);

    static Evaluable of(String str) {
        return evaluationContext -> str;
    }
}
