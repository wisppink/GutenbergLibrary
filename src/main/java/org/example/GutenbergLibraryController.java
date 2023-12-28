package org.example;

import org.example.LibraryService.Remote.GutendexService;
import org.example.LibraryService.Remote.LibraryServiceRemoteDataSource;
import org.example.LibraryService.Remote.Model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GutenbergLibraryController {

    private final GutendexService gutendexService;

    @Autowired
    public GutenbergLibraryController(GutendexService gutendexService) {
        this.gutendexService = gutendexService;
    }

    @GetMapping("/searchBooks")
    public String searchBooks(@RequestParam(value = "query", defaultValue = "") String query) {
        List<String> titles = gutendexService.searchBooks(query);

        // Concatenate titles into a single line
        StringBuilder result = new StringBuilder();
        for (String title : titles) {
            result.append(title).append(" ");
        }

        // Trim the trailing space and return the result
        return result.toString().trim();
    }
}
