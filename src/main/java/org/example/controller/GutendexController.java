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
    public String addToLibrary(Model model, @RequestParam Long bookId, Authentication authentication) {
        logger.info("add to library book id: " + bookId);
        // Retrieve the authenticated user
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            // Fetch the user from the database
            User user = userService.findUserByEmail(username);

            // Check if the book is already in the user's library
            if (user.getBooks().stream().anyMatch(book -> book.getId().equals(bookId))) {
                // Handle the case where the book is already in the library
                return "redirect:/books/library?error=alreadyInLibrary";
            }

            // Fetch book details from the external service
            LibBook book = gutendexService.getBookDetailsAsLibBook(bookId);

            // Check if the book details were successfully retrieved
            if (book != null) {
                for (int i = 0; i < user.getBooks().size(); i++) {
                    if (bookId == book.getApiId()) {
                        return "redirect:/books/library";
                    }
                }
                // Add the book to the user's library
                user.getBooks().add(book);

                // Update the user's library in the database
                UserDto userDto = userMapper.mapToDto(user);
                userService.updateUserLibrary(userDto);
                model.addAttribute("bookId", bookId);
                return "redirect:/books/library";
            } else {
                // Handle the case where book details couldn't be retrieved
                return "redirect:/books/library?error=bookDetailsNotFound";
            }
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
            logger.info("remove id: " + bookId);
            // Remove the book from the user's library
            for (int i = 0; i < user.getBooks().size(); i++) {
                LibBook book = user.getBooks().get(i);
                if (bookId == book.getApiId()) {
                    user.getBooks().remove(i);
                }
            }
            // Save the updated user entity
            userService.updateUserLibrary(userMapper.mapToDto(user));

            return "redirect:/books/library";
        } else {
            // Redirect to the login page or handle as appropriate
            return "redirect:/login";
        }
    }

    @PostMapping("/readTheBook")
    public String readTheBook(Model model, HttpSession session, @RequestParam int bookId, Authentication authentication) {
        logger.info("readTheBook requested param: " + bookId);
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Book book = gutendexService.getBookDetails((long) bookId);
            Long userId = userService.getUserId(email);
            int lastPage = gutendexService.findTheBooksLastPage(bookId, userId);
            if (lastPage < 0) {
                return "redirect:/error";
            }

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
                model.addAttribute("bookId", bookId);
                session.setAttribute("bookId", bookId);
                // Set the current page index in the model
                session.setAttribute("currentPageIndex", lastPage);
                return readContentOfTheBook(model, session, authentication);
            } else {
                // Handle the case where no suitable format is available
                return "books/noFormatAvailable";
            }
        } else {
            // Handle the case where user is not authenticated
            return "redirect:/login";
        }
    }

    @GetMapping("/read")
    public String readContentOfTheBook(Model model, HttpSession session, Authentication authentication) {
        // Retrieve pages and current page index from the session
        int lastPage = -1;
        List<String> pages = (List<String>) session.getAttribute("bookPages");
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            int id = (int) model.getAttribute("bookId");
            Long userId = userService.getUserId(email);
            lastPage = gutendexService.findTheBooksLastPage(id, userId);
            logger.info("readContent lastPage: " + lastPage);
            if (lastPage < 0) {
                return "redirect:/error";
            }
        } else {
            // Handle the case where user is not authenticated
            return "redirect:/login";
        }

        // Check if pages and currentPageIndex are available in the session
        if (pages != null && lastPage < pages.size()) {
            // Get the content of the current page
            String currentPageContent = pages.get(lastPage);
            logger.info("currentpageContent: " + currentPageContent);

            // Set the current page content, index, and bookId in the model and session
            model.addAttribute("currentPageContent", currentPageContent);
            session.setAttribute("currentPageIndex", lastPage);
            model.addAttribute("bookId", session.getAttribute("bookId")); // Add this line if needed

            return "books/read";
        } else {
            // Log details about the error
            if (pages == null) {
                logger.error("Error: Pages are not available in the session.");
            } else {
                logger.error("Error: Invalid current page index or pages size exceeded. Index: " +
                        lastPage + ", Total Pages: " + pages.size());
            }

            // Handle the case where pages or currentPageIndex is not available or index is out of bounds
            return "books/error";
        }
    }


    @PostMapping("/nextPage")
    public String nextPage(Model model, HttpSession session, Authentication authentication) {
        // Retrieve pages and current page index from the session
        List<String> pages = (List<String>) session.getAttribute("bookPages");
        Integer currentPageIndex = (Integer) session.getAttribute("currentPageIndex");

        // Check if pages and currentPageIndex are available in the session
        if (pages != null && currentPageIndex != null && currentPageIndex < pages.size() - 1) {
            // Increment the current page index
            int nextPageIndex = currentPageIndex + 1;
            int id = (int) session.getAttribute("bookId");
            userService.updateLastPageForBookInLibrary(getUserDto(authentication.getName()), id, currentPageIndex);
            // Get the content of the next page
            String nextPageContent = pages.get(nextPageIndex);
            // Set the next page content, index, and bookId in the model and session
            model.addAttribute("currentPageContent", nextPageContent);
            session.setAttribute("currentPageIndex", nextPageIndex);
            model.addAttribute("bookId", session.getAttribute("bookId")); // Add this line if needed

            return "books/read";
        } else {
            // Log details about the error
            if (pages == null) {
                logger.error("Error: Pages are not available in the session.");
            } else {
                logger.error("Error: Invalid current page index or pages size exceeded. Index: " +
                        currentPageIndex + ", Total Pages: " + (pages != null ? pages.size() : "null"));
            }

            // Handle the case where pages or currentPageIndex is not available or index is out of bounds
            return "books/error";
        }
    }


    @PostMapping("/previousPage")
    public String previousPage(Model model, HttpSession session, Authentication authentication) {
        // Retrieve pages and current page index from the session
        List<String> pages = (List<String>) session.getAttribute("bookPages");
        Integer currentPageIndex = (Integer) session.getAttribute("currentPageIndex");

        // Check if pages and currentPageIndex are available in the session
        if (pages != null && currentPageIndex != null && currentPageIndex > 0) {
            // Decrement the current page index
            int previousPageIndex = currentPageIndex - 1;
            int id = (int) session.getAttribute("bookId");
            userService.updateLastPageForBookInLibrary(getUserDto(authentication.getName()), id, currentPageIndex);
            // Get the content of the previous page
            String previousPageContent = pages.get(previousPageIndex);
            // Set the previous page content, index, and bookId in the model and session
            model.addAttribute("currentPageContent", previousPageContent);
            session.setAttribute("currentPageIndex", previousPageIndex);
            model.addAttribute("bookId", session.getAttribute("bookId")); // Add this line if needed

            return "books/read";
        } else {
            // Handle the case where there are no previous pages
            // You may want to redirect to the first page or handle it in a way that fits your requirements
            return "books/error";
        }
    }

    public UserDto getUserDto(String username) {
        User user = userService.findUserByEmail(username);
        return userMapper.mapToDto(user);
    }
}
