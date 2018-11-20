package playground;

import org.deletethis.search.parser.EngineParseException;
import org.deletethis.search.parser.SearchEngine;
import org.deletethis.search.parser.SearchQuery;
import org.deletethis.search.parser.SuggestionParseException;
import org.deletethis.search.parser.SuggestionRequest;
import org.deletethis.search.parser.opensearch.OpenSearchFactory;

import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws IOException, SuggestionParseException, EngineParseException {
        InputStream is = Main.class.getResourceAsStream("/org/deletethis/search/parser/opensearch/google.xml");
        SearchEngine searchEngine = OpenSearchFactory.loadOpenSearch(is);

        System.out.println("Name: " + searchEngine.getName());
        System.out.println("Icons: " + searchEngine.getIconUrls());
        System.out.println("Update URL: " + searchEngine.getUpdateUrl());
        System.out.println(searchEngine.getSearchUrl(SearchQuery.of("hello world")));
        SuggestionRequest suggestion = searchEngine.getSuggestions(SearchQuery.of("hello world"));

        System.out.println(suggestion.getUri());
        System.out.println(suggestion.parseResult("[\"aa\", [\"A\",\"B\"]]"));
    }
}
