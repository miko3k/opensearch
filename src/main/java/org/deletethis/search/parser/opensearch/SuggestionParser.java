package org.deletethis.search.parser.opensearch;

import org.deletethis.search.parser.Suggestion;
import org.deletethis.search.parser.SuggestionParseException;

import javax.json.stream.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

class SuggestionParser {
    private final JsonParser parser;
    private final String prefix;

    SuggestionParser(JsonParser parser, String prefix) {
        this.parser = parser;
        this.prefix = prefix;
    }

    private void swallow(JsonParser.Event wanted) throws SuggestionParseException {
        JsonParser.Event next = parser.next();
        if(next != wanted) {
            throw new SuggestionParseException("Parse error: want " + wanted + ", have " + next);
        }
    }

    private void skipObject() {
        while(true) {
            JsonParser.Event next = parser.next();
            if(next == JsonParser.Event.START_OBJECT) {
                skipObject();
                next = parser.next();
            }
            if(next == JsonParser.Event.END_OBJECT) {
                return;
            }
        }

    }

    private List<String> readStrings() throws SuggestionParseException {
        JsonParser.Event next = parser.next();
        if(next == JsonParser.Event.START_OBJECT) {
            skipObject();
            next = parser.next();
        }
        if(next == JsonParser.Event.END_ARRAY)
            return null;

        if(next != JsonParser.Event.START_ARRAY) {
            throw new SuggestionParseException("Parse error: want start or end of array, have " + next);
        }
        List<String> result = new ArrayList<>(32);

        while((next = parser.next()) == JsonParser.Event.VALUE_STRING) {
            result.add(parser.getString());
        }
        if(next != JsonParser.Event.END_ARRAY) {
            throw new SuggestionParseException("Parse error: want end of array, have " + next);
        }
        return result;

    }

    private String getElement(List<String> str, int idx) {
        if(str == null)
            return null;
        if(idx >= str.size())
            return null;
        String s = str.get(idx);
        if(s.isEmpty())
            return null;

        return s;
    }

    List<Suggestion> parseSuggestions() throws SuggestionParseException{
        try {
            swallow(JsonParser.Event.START_ARRAY);
            swallow(JsonParser.Event.VALUE_STRING);

            List<String> completions = readStrings();
            if(completions == null) {
                throw new SuggestionParseException("No completions");
            }
            List<String> descriptions = readStrings();
            List<String> urls = descriptions == null ? null : readStrings();

            int count = completions.size();
            List<Suggestion> result = new ArrayList<>(count);
            for(int i = 0; i < count; ++i) {
                Suggestion s = new OpenSearchSuggestion(
                    completions.get(i),
                    getElement(urls, i),
                    getElement(descriptions, i),
                    prefix,
                    String.valueOf(i)
                );
                result.add(s);
            }
            return result;
        } catch(NoSuchElementException ex) {
            throw new SuggestionParseException("Premature end of suggestion stream", ex);
        }
    }
}
