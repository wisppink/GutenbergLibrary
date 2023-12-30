package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.service.GutendexService;
import org.example.service.Model.BookList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/books")
public class GutendexController {

    private final GutendexService gutendexService;
    private static final Logger logger = LoggerFactory.getLogger(GutendexController.class);

    @Autowired
    public GutendexController(GutendexService gutendexService) {
        this.gutendexService = gutendexService;
    }

    @GetMapping("/search")
    public BookList searchBooks(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);
            logger.info("User authenticated: {}", username);
        }
        return gutendexService.searchBooks();
    }

    @GetMapping("/searchBooksWithFilter")
    public String getSearchFilterInput(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);
            logger.info("searchFilterInput: {}", username);
        }
        return "books/searchFilterInput";
    }

    @PostMapping("/searchBooksWithFilter")
    public String handleSearchForm(Model model, @RequestParam int switchCase, @RequestParam String inputString, HttpSession session, Authentication authentication) {
        session.setAttribute("switchCase", switchCase);
        session.setAttribute("inputString", inputString);
        // Add authenticated user information if available
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);
            logger.info("searchBooksWithFilter: {}", username);
        }
        return "redirect:/books/results";
    }


    @GetMapping("/results")
    public String showResults(Model model, HttpSession session, Authentication authentication) {
        // Retrieve form data from the session
        int switchCase = (int) session.getAttribute("switchCase");
        String inputString = (String) session.getAttribute("inputString");

        // Invoke the service method and get the results
        BookList searchResults = gutendexService.searchBooksWithFilter(switchCase, inputString);

        model.addAttribute("searchResults", searchResults);
        model.addAttribute("switchCase", switchCase);
        model.addAttribute("inputString", inputString);

        // Add authenticated user information if available
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);
            logger.info("User authenticated: {}", username);
        }

        // Render the results page
        return "books/results";
    }


}
