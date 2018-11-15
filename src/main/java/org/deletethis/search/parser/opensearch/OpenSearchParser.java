package org.deletethis.search.parser.opensearch;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class OpenSearchParser implements ElementParser {
    private String shortName;
    private String description;
    private Template selfUrl;
    private Template resultsUrl;
    private Template suggestionsUrl;
    private String contact;
    private List<String> tags = new ArrayList<>();
    private String longName;
    private List<String> images = new ArrayList<>();
    private String developer;
    private String attribution;
    private Boolean adultContent = null;
    private List<String> languages = new ArrayList<>();
    private List<Charset> inputEncodings = new ArrayList<>();
    private List<Charset> outputEncodings = new ArrayList<>();
    private boolean hasNonGet = false;

    private boolean isMediaTypeOneOf(String mediaType, String ... options) {
        for(String o: options) {
            if(mediaType.equalsIgnoreCase(o))
                return true;
        }
        return false;
    }

    private static final List<String> booleanNos = Collections.unmodifiableList(Arrays.asList(
            "false", "FALSE", "0", "no", "NO"
    ));

    private void checkSyndicationRight(String s) throws OpenSearchParseError {
        if("private".equals(s) || "closed".equals(s)) {
            throw new OpenSearchParseError("Syndication right does not allow usage of this plugin");
        }
    }

    private ElementParser url(AttributeResolver attributes, NamespaceResolver namespaces) throws OpenSearchParseError {
        String type = attributes.getValue("type");
        String rel = attributes.getValue("rel");
        String method = attributes.getValue(Constants.PARAMETERS_NAMESPACE, "method");

        if(method == null) {
            // also allow method without namespace
            method = attributes.getValue("method");
        }

        if(type == null) throw new OpenSearchParseError("Url without type");

        if(method != null && !"GET".equalsIgnoreCase(method)) {
            hasNonGet = true;
            return NOP;
        }
        List<String> rels;
        if(rel == null) {
            rels = Collections.emptyList();
        } else {
            rels = Arrays.asList(rel.split("\\s+"));
        }

        if((rels.isEmpty() || rels.contains("results")) && isMediaTypeOneOf(type, "text/html", "application/xhtml+xml")) {
            return new OpenSearchUrlParser(attributes, namespaces, (url) -> resultsUrl = url);
        } else
        if((rels.isEmpty() || rels.contains("suggestions")) && isMediaTypeOneOf(type, "application/x-suggestions+json")) {
            return new OpenSearchUrlParser(attributes, namespaces, (url) -> suggestionsUrl = url);
        } else
        if((rels.isEmpty() || rels.contains("self")) && isMediaTypeOneOf(type, "application/opensearchdescription+xml")) {
            return new OpenSearchUrlParser(attributes, namespaces, (url) -> selfUrl = url);
        } else {
            return NOP;
        }
    }

    @Override
    public ElementParser startElement(String namespace, String localName, AttributeResolver attributes, NamespaceResolver namespaces) throws OpenSearchParseError {
        if(!namespace.equals(Constants.MAIN_NAMESPACE))
            return NOP;

        switch (localName) {
            case "ShortName": return new TextParser((s) -> shortName = s);
            case "Description": return new TextParser((s) -> description = s);
            case "Url": return url(attributes, namespaces);
            case "Contact": return new TextParser((s) -> contact = s);
            case "Tags": return new TextParser((s) -> tags.addAll(Arrays.asList(s.split("\\s+"))));
            case "LongName": return new TextParser((s) -> longName = s);
            // we ignore all the attributes, they are optional so we cannot rely on them ... also .ico can have multiple sizes
            case "Image": return new TextParser((s) -> images.add(s));
            case "Query": return NOP; // we will just ignore this for now
            case "Developer": return new TextParser((s) -> developer = s);
            case "Attribution": return new TextParser((s) -> attribution = s);
            case "SyndicationRight": return new TextParser(this::checkSyndicationRight);
            case "AdultContent": return new TextParser((s) -> adultContent = !booleanNos.contains(s));
            case "Language": return new TextParser((s) -> languages.add(s));
            case "InputEncoding": return new TextParser((s) -> inputEncodings.add(Charset.forName(s)));
            case "OutputEncoding": return new TextParser((s) -> outputEncodings.add(Charset.forName(s)));
            default: throw new OpenSearchParseError("Unrecognized element");

        }
    }

    OpenSearch getOpenSearch() throws OpenSearchParseError {
        if(resultsUrl == null) {
            if(hasNonGet) {
                throw new OpenSearchParseError("Unsupported method, only GET is supported");
            } else {
                throw new OpenSearchParseError("No URL specified");
            }
        }
        if(languages.isEmpty()) languages.add("*");
        if(inputEncodings.isEmpty()) inputEncodings.add(StandardCharsets.UTF_8);
        if(outputEncodings.isEmpty()) outputEncodings.add(StandardCharsets.UTF_8);

        return new OpenSearch(
                shortName,
                description,
                selfUrl,
                resultsUrl,
                suggestionsUrl,
                contact,
                tags,
                longName,
                images,
                developer,
                attribution,
                adultContent,
                languages,
                inputEncodings,
                outputEncodings);
    }
}
