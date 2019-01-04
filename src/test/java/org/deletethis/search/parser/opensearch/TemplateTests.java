package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.PluginParseException;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class TemplateTests {
    private static final TemplateParamResolver RESOLVER = (namespacePrefix, localPart) -> {
        if(namespacePrefix == null)
            namespacePrefix = "NULL";

        switch (namespacePrefix + ":" + localPart) {
            case "NULL:a": return Evaluable.of("A");
            case "NULL:searchTerms": return Evaluable.of("search terms");
            case "SP:Q": return Evaluable.of("?");
            default: return null;
        }
    };

    String ev(String template) throws PluginParseException {
        EvaluationContext ctx = new EvaluationContext(
                "",
                StandardCharsets.UTF_8,
                StandardCharsets.UTF_8,
                "",
                "");

        return new Template(template, RESOLVER).evaluate(ctx);
    }

    @Test
    public void template() throws PluginParseException {
        Assert.assertEquals("hello", ev("hello"));
        Assert.assertEquals("A", ev("{a}"));
        Assert.assertEquals("", ev("{b?}"));
        Assert.assertEquals("http://www.google.com?q=search+terms", ev("http://www.google.com?q={searchTerms}"));
        Assert.assertEquals("a%7Cb", ev("a|b"));
        Assert.assertEquals("%3F", ev("{SP:Q}"));
        Assert.assertEquals("%3F%3F", ev("{SP:Q}{SP:Q}{UNDEFINED:UNDEFINED?}"));
    }
}
