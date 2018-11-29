package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.ErrorCode;
import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.internal.xml.AttributeResolver;
import org.deletethis.search.parser.internal.xml.ElementParser;
import org.deletethis.search.parser.internal.xml.NamespaceResolver;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OpenSearchParser implements ElementParser {
    String shortName;
    String description;
    Template selfUrl;
    Template resultsUrl;
    Template suggestionsUrl;
    String contact;
    String longName;
    List<String> images = new ArrayList<>();
    String developer;
    String attribution;
    String searchForm;
    Boolean adultContent = null;
    List<String> languages = new ArrayList<>();
    List<Charset> inputEncodings = new ArrayList<>();
    List<Charset> outputEncodings = new ArrayList<>();
    boolean hasNonGet = false;

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

    private void checkSyndicationRight(String s) throws PluginParseException {
        if("private".equals(s) || "closed".equals(s)) {
            throw new PluginParseException(ErrorCode.USAGE_NOT_ALLOWED, "Syndication right does not allow usage of this plugin");
        }
    }

    private ElementParser url(AttributeResolver attributes, NamespaceResolver namespaces) throws PluginParseException {
        String type = attributes.getValue("type");
        String rel = attributes.getValue("rel");
        String method = attributes.getValue(OpenSearchConstants.PARAMETERS_NAMESPACE, "method");

        if(method == null) {
            // also allow method without namespace
            method = attributes.getValue("method");
        }

        if(type == null) {
            return NOP;
        }

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

    public ElementParser startElementInMainNamespace(String localName, AttributeResolver attributes, NamespaceResolver namespaces) throws PluginParseException {
        switch (localName) {
            case "ShortName": return new TextParser((s) -> shortName = s);
            case "Description": return new TextParser((s) -> description = s);
            case "Url": return url(attributes, namespaces);
            case "Contact": return new TextParser((s) -> contact = s);
            case "Tags": return NOP;//return new TextParser((s) -> tags.addAll(Arrays.asList(s.split("\\s+"))));
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
            // youtube on plugin on mycroftproject is using this without moz: prefix
            case "SearchForm": return new TextParser((s) -> searchForm = s);
            default: throw new PluginParseException(ErrorCode.BAD_SYNTAX, "Unknown element: " + localName);

        }
    }

    public ElementParser startElementInMozNamespace(String localName, AttributeResolver attributes, NamespaceResolver namespaces) throws PluginParseException {
        switch (localName) {
            case "SearchForm": return new TextParser((s) -> searchForm = s);
            default: throw new PluginParseException(ErrorCode.BAD_SYNTAX, "Unknown element: " + localName);

        }
    }

    @Override
    public ElementParser startElement(String namespace, String localName, AttributeResolver attributes, NamespaceResolver namespaces) throws PluginParseException {
        switch (namespace) {
            case OpenSearchConstants.MAIN_NAMESPACE:
                return startElementInMainNamespace(localName, attributes, namespaces);

            case OpenSearchConstants.MOZ_NAMESPACE:
                return startElementInMozNamespace(localName, attributes, namespaces);

            default:
                return NOP;
        }
    }
}
