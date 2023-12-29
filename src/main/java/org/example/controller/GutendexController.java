package org.example.controller;

import org.example.service.GutendexService;
import org.example.service.Model.BookList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class GutendexController {

    private final GutendexService gutendexService;

    @Autowired
    public GutendexController(GutendexService gutendexService) {
        this.gutendexService = gutendexService;
    }

    @GetMapping("/search")
    public BookList searchBooks() {
        return gutendexService.searchBooks();
    }

    @RequestMapping(value = "/searchBooksWithFilter", method = RequestMethod.GET)
    public ResponseEntity<BookList> searchBooksWithFilter(
            @RequestParam(name = "filterType", defaultValue = "0") int filterType,
            @RequestParam(name = "filterParam", defaultValue = "") String filterParam) {

        BookList result = gutendexService.searchBooksWithFilter(filterType, filterParam);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
