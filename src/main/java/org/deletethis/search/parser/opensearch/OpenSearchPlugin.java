package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.*;
import org.deletethis.search.parser.util.ByteArrays;

import javax.json.spi.JsonProvider;
import javax.json.stream.JsonParser;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class OpenSearchPlugin implements SearchPlugin {
    private static JsonProvider jsonProvider;

    private final byte [] source;
    private final String identifier;
    private final String shortName;
    private final String selfUrl;
    private final Template resultsUrl;
    private final Template suggestionsUrl;
    private final UrlIconAddress images;
    private final List<String> languages;
    private final List<Charset> inputEncodings;
    private final List<Charset> outputEncodings;
    private final Map<PropertyName, PropertyValue> properties;

    public OpenSearchPlugin(OpenSearchParser p, byte [] source) throws PluginParseException {
        if(p.resultsUrl == null) {
            if(p.hasNonGet) {
                throw new PluginParseException(ErrorCode.INVALID_METHOD, "Unsupported method, only GET is supported");
            } else {
                throw new PluginParseException(ErrorCode.NO_URL, "No URL specified");
            }
        }
        this.languages = new ArrayList<>(p.languages);
        this.inputEncodings = new ArrayList<>(p.inputEncodings);
        this.outputEncodings = new ArrayList<>(p.outputEncodings);

        if(languages.isEmpty()) languages.add("*");
        if(inputEncodings.isEmpty()) inputEncodings.add(StandardCharsets.UTF_8);
        if(outputEncodings.isEmpty()) outputEncodings.add(StandardCharsets.UTF_8);

        this.source = Objects.requireNonNull(source);
        this.shortName = Objects.requireNonNull(p.shortName);
        this.resultsUrl = Objects.requireNonNull(p.resultsUrl);
        this.suggestionsUrl = p.suggestionsUrl;
        this.images = UrlIconAddress.of(p.images);

        Map<PropertyName, PropertyValue> prop = new LinkedHashMap<>();

        if(p.longName != null)
            prop.put(PropertyName.LONG_NAME, new PropertyValue.Literal(p.longName));
        if(p.description != null)
            prop.put(PropertyName.DESCRIPTION, new PropertyValue.Literal(p.description));
        if(p.contact != null)
            prop.put(PropertyName.CONTACT, new PropertyValue.Url("mailto:" + p.contact, p.contact));
        if(p.searchForm != null)
            prop.put(PropertyName.SEARCH_FORM, new PropertyValue.Url(p.searchForm, p.searchForm));
        if(p.developer != null)
            prop.put(PropertyName.DEVELOPER, new PropertyValue.Literal(p.developer));
        if(p.attribution != null)
            prop.put(PropertyName.ATTRIBUTION, new PropertyValue.Literal(p.attribution));
        if(p.adultContent != null)
            prop.put(PropertyName.ADULT_CONTENT, p.adultContent ? PropertyValue.Predefined.YES : PropertyValue.Predefined.NO);

        this.properties = Collections.unmodifiableMap(prop);

        if(p.selfUrl != null) {
            this.selfUrl = p.selfUrl.evaluate(createEvaluation("", "", ""));
        } else {
            this.selfUrl = null;
        }
        if(p.selfUrl != null) {
            this.identifier = this.selfUrl;
        } else {
            this.identifier = ByteArrays.sha1Sum(source);
        }
    }

    @Override
    public String toString() {
        return "OpenSearchPlugin{" +
                ", identifier='" + identifier + '\'' +
                ", shortName='" + shortName + '\'' +
                ", selfUrl=" + selfUrl +
                ", resultsUrl=" + resultsUrl +
                ", suggestionsUrl=" + suggestionsUrl +
                ", images=" + images +
                ", languages=" + languages +
                ", inputEncodings=" + inputEncodings +
                ", outputEncodings=" + outputEncodings +
                ", properties=" + properties +
                '}';
    }

    @Override
    public String getName() {
        return shortName;
    }

    private EvaluationContext createEvaluation(String searchTerms, String suggestionPrefix, String suggestionIndex) {
        return new EvaluationContext(searchTerms, inputEncodings.get(0), outputEncodings.get(0), suggestionPrefix, suggestionIndex);
    }

    private EvaluationContext createEvaluation(SearchQuery search) {
        if(search.getSuggestion() != null) {
            OpenSearchSuggestion oss = (OpenSearchSuggestion) search.getSuggestion();
            return createEvaluation(oss.getValue(), oss.getSuggestionPrefix(), oss.getSuggestionIndex());
        } else {
            return createEvaluation(search.getValue(), "", "");
        }
    }

    @Override
    public Request getSearchRequest(SearchQuery search) {
        return new BasicRequest(resultsUrl.evaluate(createEvaluation(search)));
    }

    @Override
    public HttpMethod getSearchMethod() {
        return HttpMethod.GET;
    }

    @Override
    public Optional<String> getUpdateUrl() {
        return Optional.ofNullable(selfUrl);
    }

    @Override
    public UrlIconAddress getIcon() {
        return images;
    }


    @Override
    public boolean supportsSuggestions() {
        return suggestionsUrl != null;
    }

    @Override
    public HttpMethod getSuggestionMethod() {
        return HttpMethod.GET;
    }

    private List<Suggestion> parseSuggestions(String prefix, String body) throws SuggestionParseException {
        if(jsonProvider == null) {
            jsonProvider = JsonProvider.provider();
        }
        JsonParser parser = jsonProvider.createParser(new StringReader(body));
        SuggestionParser suggestionParser = new SuggestionParser(
                parser,
                prefix
        );

        try {
            return suggestionParser.parseSuggestions();
        } catch (SuggestionParseException ex) {
            throw new SuggestionParseException(parser.getLocation() + ": " + ex.getMessage(), ex);
        }
    }

    abstract private static class BasicRequestSuggestionParser extends BasicRequest implements SuggestionRequest {
        BasicRequestSuggestionParser(String url) {
            super(url);
        }
    }

    @Override
    public SuggestionRequest getSuggestionRequest(SearchQuery searchQuery) {
        if(suggestionsUrl == null) {
            throw new UnsupportedOperationException();
        }

        return new BasicRequestSuggestionParser(suggestionsUrl.evaluate(createEvaluation(searchQuery))) {
            @Override
            public List<Suggestion> parseResult(String body) throws SuggestionParseException {
                return parseSuggestions(searchQuery.getAnyValue(), body);
            }
        };
    }

    @Override
    public Map<PropertyName, PropertyValue> getProperties() {
        return properties;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public byte[] serialize() {
        return source;
    }

    @Override
    public PatchBuilder patch() {
        return new PatchBuilder().plugin(this);
    }

    @Override
    public Map<String, String> getAttributes() {
        return Collections.emptyMap();
    }
}
