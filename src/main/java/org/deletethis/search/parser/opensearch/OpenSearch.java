package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.*;

import javax.json.spi.JsonProvider;
import javax.json.stream.JsonParser;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.*;

class OpenSearch implements SearchEngine {
    private static JsonProvider jsonProvider;

    private final String shortName;
    private final String description;
    private final Template selfUrl;
    private final Template resultsUrl;
    private final Template suggestionsUrl;
    private final String contact;
    private final List<String> tags;
    private final String longName;
    private final AddressList images;
    private final String developer;
    private final String attribution;
    private final Boolean adultContent;
    private final List<String> languages;
    private final List<Charset> inputEncodings;
    private final List<Charset> outputEncodings;
    private transient Map<PropertyName, PropertyValue> cachedProperties;


    public OpenSearch(String shortName, String description, Template selfUrl, Template resultsUrl, Template suggestionsUrl, String contact, List<String> tags, String longName, AddressList images, String developer, String attribution, Boolean adultContent, List<String> languages, List<Charset> inputEncodings, List<Charset> outputEncodings) {
        if(inputEncodings.isEmpty() || outputEncodings.isEmpty())
            throw new IllegalArgumentException();

        this.shortName = Objects.requireNonNull(shortName);
        this.description = description;
        this.selfUrl = selfUrl;
        this.resultsUrl = Objects.requireNonNull(resultsUrl);
        this.suggestionsUrl = suggestionsUrl;
        this.contact = contact;
        this.tags = tags;
        this.longName = longName;
        this.images = images;
        this.developer = developer;
        this.attribution = attribution;
        this.adultContent = adultContent;
        this.languages = languages;
        this.inputEncodings = inputEncodings;
        this.outputEncodings = outputEncodings;
    }

    @Override
    public String toString() {
        return "OpenSearch{" +
                "shortName='" + shortName + '\'' +
                ", description='" + description + '\'' +
                ", selfUrl=" + selfUrl +
                ", resultsUrl=" + resultsUrl +
                ", suggestionsUrl=" + suggestionsUrl +
                ", contact='" + contact + '\'' +
                ", tags=" + tags +
                ", longName='" + longName + '\'' +
                ", images=" + images +
                ", developer='" + developer + '\'' +
                ", attribution='" + attribution + '\'' +
                ", adultContent=" + adultContent +
                ", languages=" + languages +
                ", inputEncodings=" + inputEncodings +
                ", outputEncodings=" + outputEncodings +
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
    public String getSearchUrl(SearchQuery search) {
        return resultsUrl.evaluate(createEvaluation(search));
    }

    @Override
    public Optional<String> getUpdateUrl() {
        if(selfUrl == null) {
            return Optional.empty();
        } else {
            return Optional.of(selfUrl.evaluate(createEvaluation("", "", "")));
        }
    }

    @Override
    public Optional<AddressList> getIconAddress() {
        return Optional.ofNullable(images);
    }


    @Override
    public boolean supportsSuggestions() {
        return suggestionsUrl != null;
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

    @Override
    public SuggestionRequest getSuggestions(SearchQuery searchQuery) {
        if(suggestionsUrl == null) {
            throw new UnsupportedOperationException();
        }

        return new SuggestionRequest() {
            private String cache;
            @Override
            public String getUri() {
                if(cache == null) {
                    cache = suggestionsUrl.evaluate(createEvaluation(searchQuery));
                }
                return cache;
            }

            @Override
            public List<Suggestion> parseResult(String body) throws SuggestionParseException {
                return parseSuggestions(searchQuery.getAnyValue(), body);
            }
        };
    }

    private Map<PropertyName, PropertyValue> prepareProperties() {
        Map<PropertyName, PropertyValue> result = new LinkedHashMap<>();

        if(longName != null)
            result.put(PropertyName.LONG_NAME, new PropertyValue.Literal(longName));
        if(description != null)
            result.put(PropertyName.DESCRIPTION, new PropertyValue.Literal(description));
        if(contact != null)
            result.put(PropertyName.CONTACT, new PropertyValue.Literal(contact));
        if(developer != null)
            result.put(PropertyName.DEVELOPER, new PropertyValue.Literal(developer));
        if(attribution != null)
            result.put(PropertyName.ATTRIBUTION, new PropertyValue.Literal(attribution));
        if(adultContent != null)
            result.put(PropertyName.ADULT_CONTENT, adultContent ? PropertyValue.Predefined.YES : PropertyValue.Predefined.NO);

        return result;
    }

    @Override
    public Map<PropertyName, PropertyValue> getProperties() {
        if(cachedProperties == null) {
            cachedProperties = prepareProperties();
        }
        return cachedProperties;
    }

}
