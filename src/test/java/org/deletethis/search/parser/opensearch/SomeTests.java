package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.*;
import org.junit.Assert;
import org.junit.Test;

import javax.json.Json;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SomeTests {
    private InputStream res(String name) {
        InputStream in = SomeTests.class.getResourceAsStream(name);
        if(in == null)
            throw new IllegalStateException("Unable to open stream: " + name);
        return in;
    }

    @Test
    public void googleus() throws IOException, EngineParseException {
        SearchEngine google = OpenSearchFactory.loadOpenSearch(res("google.xml"));

        Assert.assertEquals("Google US", google.getName());
        Assert.assertTrue(google.supportsSuggestions());
        Map<PropertyName, PropertyValue> properties = google.getProperties();

        Assert.assertEquals(new PropertyValue.Literal("Google US"), properties.get(PropertyName.DESCRIPTION));
        Assert.assertEquals(new PropertyValue.Literal("mycroft.mozdev.org@googlemail.com"), properties.get(PropertyName.CONTACT));
        Assert.assertNull(properties.get(PropertyName.LONG_NAME));
        Assert.assertEquals(new PropertyValue.Literal("Mycroft Project"), properties.get(PropertyName.DEVELOPER));
        Assert.assertNull(properties.get(PropertyName.ATTRIBUTION));
        Assert.assertNull(properties.get(PropertyName.ADULT_CONTENT));

        Assert.assertEquals(Optional.of("https://mycroftproject.com/updateos.php/id0/googleintl.xml"), google.getUpdateUrl());
        Assert.assertEquals("https://www.google.com/search?name=f&hl=en&q=hello+world", google.getSearchUrl(SearchQuery.of("hello world")));
        Assert.assertTrue(google.getIconUrls().contains("https://mycroftproject.com/updateos.php/id0/googleintl.ico"));
        Assert.assertEquals("https://suggestqueries.google.com/complete/search?output=firefox&client=firefox&hl=en&q=hello+world", google.getSuggestions(SearchQuery.of("hello world")).getUri());
    }

    @Test
    public void googlesuggestions() throws SuggestionParseException {
        SuggestionParser suggestionParser = new SuggestionParser(Json.createParser(res("googlesuggestions.json")), "hello world");
        List<Suggestion> suggestions = suggestionParser.parseSuggestions();

        OpenSearchSuggestion oss = (OpenSearchSuggestion) suggestions.get(1);
        Assert.assertEquals(Optional.empty(), oss.getDescription());
        Assert.assertEquals(Optional.empty(), oss.getUrl());
        Assert.assertEquals("hello world java", oss.getValue());
        Assert.assertEquals("1", oss.getSuggestionIndex());
        Assert.assertEquals("hello world", oss.getSuggestionPrefix());
    }

    @Test
    public void googlesuggestions2() throws SuggestionParseException {
        SuggestionParser suggestionParser = new SuggestionParser(Json.createParser(res("googlesuggestions2.json")), "hello world");
        List<Suggestion> suggestions = suggestionParser.parseSuggestions();

        OpenSearchSuggestion oss = (OpenSearchSuggestion) suggestions.get(2);
        Assert.assertEquals(Optional.empty(), oss.getDescription());
        Assert.assertEquals(Optional.empty(), oss.getUrl());
        Assert.assertEquals("fgaddon", oss.getValue());
    }


    @Test
    public void wikisuggestions() throws SuggestionParseException {
        SuggestionParser suggestionParser = new SuggestionParser(Json.createParser(res("desuggestions.json")), "de");
        List<Suggestion> suggestions = suggestionParser.parseSuggestions();

        OpenSearchSuggestion oss = (OpenSearchSuggestion) suggestions.get(1);
        Assert.assertEquals(Optional.empty(), oss.getDescription());
        Assert.assertEquals(Optional.of("https://de.wiktionary.org/wiki/Deutsch"), oss.getUrl());
        Assert.assertEquals("Deutsch", oss.getValue());
        Assert.assertEquals("1", oss.getSuggestionIndex());
        Assert.assertEquals("de", oss.getSuggestionPrefix());
    }

}