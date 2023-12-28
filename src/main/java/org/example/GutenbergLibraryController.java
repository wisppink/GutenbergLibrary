package org.example;

import org.example.back.local.SearchBookListResponse;
import org.example.back.remote.LibraryService.Remote.GutendexService;
import org.example.ui.UiMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GutenbergLibraryController {

    private final GutendexService gutendexService;

    @Autowired
    public GutenbergLibraryController(GutendexService gutendexService) {
        this.gutendexService = gutendexService;
    }

    @GetMapping("/searchBooks")
    public String searchBooks(@RequestParam(value = "query", defaultValue = "") String query) {
        UiMapper uiMapper = new UiMapper();
        SearchBookListResponse response = uiMapper.RemoteToLocalBookList(gutendexService.searchBooks(query));

        // Concatenate titles into a single line
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < response.getResults().size(); i++) {
            result.append(response.getResults().get(i).getTitle());
        }

        // Trim the trailing space and return the result
        return result.toString().trim();
    }
}
