package playground;

import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.SearchPlugin;
import org.deletethis.search.parser.SearchPluginFactory;
import org.deletethis.search.parser.SearchQuery;
import org.deletethis.search.parser.SuggestionParseException;
import org.deletethis.search.parser.SuggestionRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException, SuggestionParseException, PluginParseException {
        InputStream is = Main.class.getResourceAsStream("/org/deletethis/search/parser/opensearch/google.xml");
        SearchPlugin searchPlugin = SearchPluginFactory.loadSearchPlugin(is);

        System.out.println("Id: " + searchPlugin.getIdentifier());
        System.out.println("Name: " + searchPlugin.getName());
        System.out.println("Icons: " + searchPlugin.getIcon());
        System.out.println("Update URL: " + searchPlugin.getUpdateUrl());
        System.out.println(searchPlugin.getSearchRequest(SearchQuery.of("hello world")));
        SuggestionRequest suggestion = searchPlugin.getSuggestionRequest(SearchQuery.of("hello world"));

        SearchPlugin patched = searchPlugin.patch().name("Other name").attr("a", "b").build();
        System.out.println("Patched name: " + patched.getName());
        System.out.println("Patched: " + new String(patched.serialize(), StandardCharsets.UTF_8));

        System.out.println(suggestion.getUrl());
        System.out.println(suggestion.parseResult("[\"aa\", [\"A\",\"B\"]]"));
    }
}
