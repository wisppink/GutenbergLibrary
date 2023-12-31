package org.example.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import org.example.dto.UserDto;
import org.example.entity.LibBook;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.GutendexService;
import org.example.service.Model.BookList;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/books")
public class GutendexController {

    private static final Logger logger = LoggerFactory.getLogger(GutendexController.class);
    private final GutendexService gutendexService;
    private final UserService userService;
    private final UserMapper userMapper;
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public GutendexController(GutendexService gutendexService, UserService userService, UserMapper userMapper) {
        this.gutendexService = gutendexService;
        this.userService = userService;
        this.userMapper = userMapper;
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

    @GetMapping("/library")
    public String showLibrary(Model model, Authentication authentication) {
        // Retrieve the authenticated user
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            // Fetch the user's library from the database
            User user = userService.findUserByEmail(username);
            List<LibBook> library = user.getBooks();

            model.addAttribute("username", username);
            model.addAttribute("library", library);

            return "books/library";
        } else {
            // Redirect to the login page or handle as appropriate
            return "redirect:/login";
        }
    }

    @Transactional
    @PostMapping("/addToLibrary")
    public String addToLibrary(@RequestParam Long bookId, Authentication authentication) {
        // Retrieve the authenticated user
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            // Fetch the user from the database
            User user = userService.findUserByEmail(username);
            // Fetch the book from the database (or any other data source)
            LibBook book = gutendexService.getBookDetails(bookId);

            LibBook attachedBook = entityManager.merge(book);
            logger.info("book url: " + book.getUrl());
            logger.info("before add: " + user.getBooks());
            user.getBooks().add(attachedBook);
            logger.info("after add: " + user.getBooks());
            UserDto userDto = userMapper.mapToDto(user);
            userService.updateUserLibrary(userDto);
            return "redirect:/books/library";
        } else {
            // Redirect to the login page or handle as appropriate
            return "redirect:/login";
        }
    }


    // Controller method to remove a book from the user's library
    @PostMapping("/removeFromLibrary")
    public String removeFromLibrary(@RequestParam Long bookId, Authentication authentication) {
        // Retrieve the authenticated user
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            // Fetch the user from the database
            User user = userService.findUserByEmail(username);

            // Remove the book from the user's library
            user.getBooks().removeIf(book -> book.getId().equals(bookId));

            // Save the updated user entity
            userService.saveUser(userMapper.mapToDto(user));

            return "redirect:/books/library";
        } else {
            // Redirect to the login page or handle as appropriate
            return "redirect:/login";
        }
    }

}
