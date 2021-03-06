package org.deletethis.search.parser.opensearch;

import com.google.common.collect.Iterables;
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
    public void googleus() throws IOException, PluginParseException {
        SearchPlugin google = new SearchPluginFactory().loadSearchPlugin(res("google.xml"));

        Assert.assertEquals("Google US", google.getName());
        Assert.assertTrue(google.supportsSuggestions());
        Map<PropertyName, PropertyValue> properties = google.getProperties();

        Assert.assertEquals(new PropertyValue.Literal("Google US"), properties.get(PropertyName.DESCRIPTION));
        Assert.assertEquals(new PropertyValue.Url("mailto:mycroft.mozdev.org@googlemail.com", "mycroft.mozdev.org@googlemail.com"), properties.get(PropertyName.CONTACT));
        Assert.assertNull(properties.get(PropertyName.LONG_NAME));
        Assert.assertEquals(new PropertyValue.Literal("Mycroft Project"), properties.get(PropertyName.DEVELOPER));
        Assert.assertNull(properties.get(PropertyName.ATTRIBUTION));
        Assert.assertNull(properties.get(PropertyName.ADULT_CONTENT));
        Assert.assertEquals(new PropertyValue.Url("https://www.google.com/ncr", "https://www.google.com/ncr"), properties.get(PropertyName.SEARCH_FORM));

        Assert.assertEquals(Optional.of("https://mycroftproject.com/updateos.php/id0/googleintl.xml"), google.getUpdateUrl());
        Assert.assertEquals("https://www.google.com/search?name=f&hl=en&q=hello+world", google.getSearchRequest(SearchQuery.of("hello world")).getUrl());
        Assert.assertTrue(Iterables.contains(google.getIcon(), "https://mycroftproject.com/updateos.php/id0/googleintl.ico"));
        Assert.assertEquals("https://suggestqueries.google.com/complete/search?output=firefox&client=firefox&hl=en&q=hello+world", google.getSuggestionRequest(SearchQuery.of("hello world")).getUrl());
    }

    @Test
    public void patch() throws IOException, PluginParseException {
        String name = "<>&aa";

        SearchPlugin google = new SearchPluginFactory().loadSearchPlugin(res("google.xml"));
        byte[] data = google.serialize();
        SearchPlugin bar = google.patch().name(name).build();
        byte[] xx = bar.serialize();

        Assert.assertEquals(name, bar.getName());
        Assert.assertNotSame(data, bar.serialize());
        bar = google.patch().name(null).build();
        Assert.assertEquals("Google US", bar.getName());
        Assert.assertSame(data, bar.serialize());

        SearchPlugin ee = new SearchPluginFactory().loadSearchPlugin(xx);
        Assert.assertEquals(name, ee.getName());
        SearchPlugin ee2 = ee.patch().name(null).build();
        byte[] ees = ee2.serialize();
        Assert.assertArrayEquals(data, ees);
    }

    @Test
    public void patch2() throws IOException, PluginParseException {
        String name = "<>&aa";
        String value = "<>&aaXX";

        SearchPlugin orig = new SearchPluginFactory().loadSearchPlugin(res("google.xml"));
        byte[] origData = orig.serialize();
        byte[] patchedData = orig.patch()
                .attr(name, value)
                .icon(UrlIconAddress.NONE)
                .identifier("hello")
                .build().serialize();

        SearchPlugin patched = new SearchPluginFactory().loadSearchPlugin(patchedData);
        Assert.assertEquals(1, patched.getAttributes().size());
        Assert.assertEquals(value, patched.getAttributes().get(name));
        Assert.assertEquals("hello", patched.getIdentifier());
        Assert.assertSame(UrlIconAddress.NONE, patched.getIcon());

        byte[] unpachedData = patched.patch().removeAttr(name).icon(null).identifier(null).build().serialize();
        Assert.assertArrayEquals(unpachedData, origData);
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

    @Test
    public void youtube() throws IOException, PluginParseException {
        SearchPlugin plugin = new SearchPluginFactory().loadSearchPlugin(res("youtube.xml"));

        Assert.assertEquals("YouTube", plugin.getName());
        Assert.assertEquals(new PropertyValue.Url("https://www.youtube.com/index", "https://www.youtube.com/index"), plugin.getProperties().get(PropertyName.SEARCH_FORM));
    }

}
