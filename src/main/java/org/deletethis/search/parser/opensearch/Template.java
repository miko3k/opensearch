package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.EngineParseException;
import org.deletethis.search.parser.ErrorCode;
import org.deletethis.search.parser.internal.xml.NamespaceResolver;

import javax.xml.namespace.QName;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Template implements Evaluable {
    private static class TemplateParameter {
        private final int begin;
        private final int end;
        private final Evaluable value;

        private TemplateParameter(int begin, int end, Evaluable value) {
            this.begin = begin;
            this.end = end;
            this.value = value;
        }
    }

    private final String template;
    private final List<TemplateParameter> parameters;

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

    Template(String template, NamespaceResolver namespaceResolver, Function<QName, Evaluable> paramResolver) throws EngineParseException {
        this.template = template;
        this.parameters = new ArrayList<>();

        Matcher matcher = PATTERN.matcher(template);
        while(matcher.find()) {

            boolean required = true;
            String matched = matcher.group(1);
            if(matched.endsWith("?")) {
                required = false;
                matched = matched.substring(0, matched.length()-1);
            }
            int index = matched.indexOf(':');
            String ns = null;
            if(index >= 0) {
                String prefix = matched.substring(0, index);
                matched = matched.substring(index+1);

                ns = namespaceResolver.getURI(prefix);

                if(ns == null) {
                    throw new EngineParseException(ErrorCode.BAD_SYNTAX, "No namespace is registered with prefix: " + prefix);
                }
            }
            QName name = new QName(ns, matched);
            Evaluable ev = paramResolver.apply(name);
            if(ev == null) {
                if(required) {
                    throw new EngineParseException(ErrorCode.BAD_SYNTAX, "Unknown parameter: " + name);
                } else {
                    ev = Evaluable.of("");
                }
            }

            this.parameters.add(new TemplateParameter(matcher.start(), matcher.end(), ev));
        }
    }

    @Override
    public String evaluate(EvaluationContext evaluationContext) {
        StringBuilder bld = new StringBuilder();
        int last = 0;
        for(TemplateParameter p: parameters) {
            if(last != p.begin) {
                bld.append(template, last, p.begin);
            }
            String ev = p.value.evaluate(evaluationContext);
            try {
                ev = URLEncoder.encode(ev, evaluationContext.getInputEncoding().toString());
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }

            bld.append(ev);
            last = p.end;
        }
        if(last < template.length())
            bld.append(template, last, template.length());

        return bld.toString();
    }
}
