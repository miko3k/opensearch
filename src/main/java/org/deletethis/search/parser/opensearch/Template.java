package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.ErrorCode;
import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.util.UrlCharacters;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Template implements Evaluable {

    private final List<Evaluable> parts;

    /*
     * Syntax:
     *  tparameter     = "{" tqname [ tmodifier ] "}"
     *  tqname         = [ tprefix ":" ] tlname
     *  tprefix        = *pchar
     *  tlname         = *pchar
     *  tmodifier      = "?"
     *
     *  pchar          = unreserved / pct-encoded / sub-delims / ":" / "@"
     *  unreserved     = ALPHA / DIGIT / "-" / "." / "_" / "~"
     *  pct-encoded    = "%" HEXDIG HEXDIG
     *  sub-delims     = "!" / "$" / "&" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
     *  ALPHA          =  %x41-5A / %x61-7A   ; A-Z / a-z
     *  DIGIT          =  %x30-39             ; 0-9
     *  HEXDIG         =  DIGIT / "A" / "B" / "C" / "D" / "E" / "F"
     *
     *
     * Notes:
     *  While not expressly allowed by above syntax, RFC 3986 says, that the uppercase hexadecimal digits 'A'
     *  through 'F' are equivalent to the lowercase digits 'a' through 'f' in percent encoding.
     *
     *  Colon is allowed as a part of both of prefix and local name. Which is weird. We split at first colon.
     */
    private final static Pattern PATTERN = Pattern.compile("\\{(([-._~a-zA-Z00-9]|(%[0-9a-fA-F]{2})|[!$&'(/)*+,;=]|[:@])*\\??)[}]");

    private static class UrlEncoded implements Evaluable {
        private final Evaluable raw;

        public UrlEncoded(Evaluable raw) {
            this.raw = raw;
        }

        @Override
        public String evaluate(EvaluationContext evaluationContext) {
            String val = raw.evaluate(evaluationContext);
            return UrlCharacters.encodeQuery(val, evaluationContext.getOutputEncoding());
        }
    }

    private Evaluable createParameter(String pattern, TemplateParamResolver paramResolver) throws PluginParseException {
        boolean required = true;
        if(pattern.endsWith("?")) {
            required = false;
            pattern = pattern.substring(0, pattern.length()-1);
        }
        int index = pattern.indexOf(':');
        String prefix = null;
        if(index >= 0) {
            prefix = pattern.substring(0, index);
            pattern = pattern.substring(index+1);

        }
        Evaluable ev = paramResolver.getParameterValue(prefix, pattern);
        if(ev == null) {
            if(required) {
                throw new PluginParseException(ErrorCode.BAD_SYNTAX, "Unknown parameter: " + pattern);
            } else {
                ev = Evaluable.of("");
            }
        }
        return new UrlEncoded(ev);
    }

    private Evaluable createFixed(String str, int begin, int end) {
        return Evaluable.of(SimpleUrlParser.sanitize(str.substring(begin, end)));
    }

    Template(String template, TemplateParamResolver paramResolver) throws PluginParseException {
        this.parts = new ArrayList<>();

        Matcher matcher = PATTERN.matcher(template);
        int last = 0;

        while(matcher.find()) {
            String matched = matcher.group(1);

            if(last != matcher.start()) {
                parts.add(createFixed(template, last, matcher.start()));
            }

            this.parts.add(createParameter(matched, paramResolver));

            last = matcher.end();
        }
        if(last < template.length()) {
            parts.add(createFixed(template, last, template.length()));
        }
    }

    @Override
    public String evaluate(EvaluationContext evaluationContext) {
        StringBuilder bld = new StringBuilder();
        for(Evaluable ev: parts) {
            bld.append(ev.evaluate(evaluationContext));
        }
        return bld.toString();
    }
}
