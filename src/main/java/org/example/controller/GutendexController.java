package org.example.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import org.example.dto.UserDto;
import org.example.entity.LibBook;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.GutendexService;
import org.example.service.Model.Book;
import org.example.service.Model.BookList;
import org.example.service.Model.Format;
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
            LibBook book = gutendexService.getBookDetailsAsLibBook(bookId);
            LibBook attachedBook = entityManager.merge(book);
            user.getBooks().add(attachedBook);
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
            userService.updateUserLibrary(userMapper.mapToDto(user));

            return "redirect:/books/library";
        } else {
            // Redirect to the login page or handle as appropriate
            return "redirect:/login";
        }
    }

    @PostMapping("/readTheBook")
    public String readTheBook(Model model, HttpSession session, @RequestParam int bookID) {
        Book book = gutendexService.getBookDetails((long) bookID);
        Format formats = book.getFormats();
        String selectedFormat = gutendexService.prioritizeFormats(formats);

        if (selectedFormat != null) {
            String contentUrl = formats.getFormatMap().get(selectedFormat);
            logger.info("Content url: " + contentUrl);
            // Fetch content based on the selected format
            String content = gutendexService.fetchBookContent(contentUrl);


            // Split content into pages
            List<String> pages = gutendexService.splitContentIntoPages(content);
            logger.info("controller: pages: size:  " + pages.size());

            // Save the pages and current page index in the session
            session.setAttribute("bookPages", pages);
            int currentPageIndex = 0;
            // Set the current page index in the model
            model.addAttribute("currentPageIndex", currentPageIndex);
            return readContentOfTheBook(model, session);
        } else {
            // Handle the case where no suitable format is available
            return "books/noFormatAvailable";
        }
    }

    @GetMapping("/read")
    public String readContentOfTheBook(Model model, HttpSession session) {
        // Retrieve pages and current page index from the session
        List<String> pages = (List<String>) session.getAttribute("bookPages");
        Integer currentPageIndex = (Integer) model.getAttribute("currentPageIndex");

        // Check if pages and currentPageIndex are available in the session
        if (pages != null && currentPageIndex != null && currentPageIndex < pages.size()) {
            // Get the content of the current page
            String currentPageContent = pages.get(currentPageIndex);
            logger.info("Successfully retrieved content for page " + currentPageIndex);

            // Set the current page content and index in the model
            model.addAttribute("currentPageContent", currentPageContent);
            model.addAttribute("currentPageIndex", currentPageIndex);

            return "books/read";
        } else {
            // Log details about the error
            if (pages == null) {
                logger.error("Error: Pages are not available in the session.");
            } else if (currentPageIndex == null) {
                logger.error("Error: Current page index is not available in the model.");
            } else {
                logger.error("Error: Invalid current page index or pages size exceeded. Index: " +
                        currentPageIndex + ", Total Pages: " + (pages != null ? pages.size() : "N/A"));
            }

            // Handle the case where pages or currentPageIndex is not available
            return "books/error";
        }
    }

    @PostMapping("/nextPage")
    public String nextPage(Model model, HttpSession session) {
        // Retrieve pages and current page index from the session
        logger.info("next works but not the html");
        Object pagesObject = session.getAttribute("bookPages");
        Object currentPageIndexObject = session.getAttribute("currentPageIndex");

        if (pagesObject instanceof List<?> && currentPageIndexObject instanceof Integer currentPageIndex) {
            List<String> pages = (List<String>) pagesObject;

            // Check if there are more pages
            if (currentPageIndex < pages.size() - 1) {
                // Increment the current page index
                currentPageIndex++;
                session.setAttribute("currentPageIndex", currentPageIndex);

                // Set the current page content in the model
                model.addAttribute("currentPage", pages.get(currentPageIndex));
                model.addAttribute("totalPages", pages.size());

                return readContentOfTheBook(model, session);
            } else {
                // Handle the case where there are no more pages
                return "books/noMorePages";
            }
        } else {
            // Handle the case where attributes are not of the expected types
            return "books/error";
        }
    }

    @PostMapping("/previousPage")
    public String previousPage(Model model, HttpSession session) {
        // Retrieve pages and current page index from the session
        Object pagesObject = session.getAttribute("bookPages");
        Object currentPageIndexObject = session.getAttribute("currentPageIndex");

        if (pagesObject instanceof List<?> && currentPageIndexObject instanceof Integer currentPageIndex) {
            List<String> pages = (List<String>) pagesObject;

            // Check if there are previous pages
            if (currentPageIndex > 0) {
                // Decrement the current page index
                currentPageIndex--;
                session.setAttribute("currentPageIndex", currentPageIndex);

                // Set the current page content in the model
                model.addAttribute("currentPage", pages.get(currentPageIndex));
                model.addAttribute("totalPages", pages.size());

                return readContentOfTheBook(model, session);
            } else {
                // Handle the case where there are no previous pages
                return "books/library";
            }
        } else {
            // Handle the case where attributes are not of the expected types
            return "books/error";
        }
    }


}
