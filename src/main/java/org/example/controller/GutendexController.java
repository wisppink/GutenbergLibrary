package org.example.controller;

import org.example.service.GutendexService;
import org.example.service.Model.BookList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/books")
public class GutendexController {

    private final GutendexService gutendexService;

    @Autowired
    public GutendexController(GutendexService gutendexService) {
        this.gutendexService = gutendexService;
    }

    @GetMapping("/search")
    public BookList searchBooks(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);
            System.out.println(model.getAttribute("username"));
        }
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
