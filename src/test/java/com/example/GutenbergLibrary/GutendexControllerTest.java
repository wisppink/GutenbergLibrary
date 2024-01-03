package org.example.controller;
import org.example.dto.UserDto;
import jakarta.servlet.http.HttpSession;
import org.example.entity.LibBook;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.GutendexService;
import org.example.service.Model.Book;
import org.example.service.Model.BookList;
import org.example.service.Model.Format;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.example.mapper.BookMapper;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GutendexControllerTest {

    @Mock
    private GutendexService gutendexService;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private GutendexController gutendexController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchBooks() {
        // Test for searchBooks method
        BookList mockBookList = mock(BookList.class);
        when(gutendexService.searchBooks()).thenReturn(mockBookList);
        assertEquals(mockBookList, gutendexController.searchBooks(model, null));
    }

    @Test
    void testGetSearchFilterInput() {
        // Test for getSearchFilterInput method
        assertEquals("books/searchFilterInput", gutendexController.getSearchFilterInput(model, null));
    }

    @Test
    void testHandleSearchForm() {
        // Test for handleSearchForm method
        int switchCase = 1;
        String inputString = "input";
        String expectedRedirect = "redirect:/books/results";
        when(session.getAttribute("switchCase")).thenReturn(switchCase);
        when(session.getAttribute("inputString")).thenReturn(inputString);
        assertEquals(expectedRedirect, gutendexController.handleSearchForm(model, switchCase, inputString, session, null));
    }

    @Test
    void testShowResults() {
        // Test for showResults method
        int switchCase = 1;
        String inputString = "input";
        BookList mockBookList = mock(BookList.class);
        when(session.getAttribute("switchCase")).thenReturn(switchCase);
        when(session.getAttribute("inputString")).thenReturn(inputString);
        when(gutendexService.searchBooksWithFilter(switchCase, inputString)).thenReturn(mockBookList);
        assertEquals("books/results", gutendexController.showResults(model, session, null));
        verify(model).addAttribute("searchResults", mockBookList);
    }

    @Test
    void testShowLibrary() {
        // Test for showLibrary method
        Authentication authentication = mock(Authentication.class);
        User mockUser = mock(User.class);
        List<LibBook> mockLibrary = new ArrayList<>();
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser@example.com");
        when(userService.findUserByEmail(anyString())).thenReturn(mockUser);
        when(mockUser.getBooks()).thenReturn(mockLibrary);
        assertEquals("books/library", gutendexController.showLibrary(model, authentication));
        verify(model).addAttribute("username", "testuser@example.com");
        verify(model).addAttribute("library", mockLibrary);
    }

    @Test
    void testAddToLibrary() {
        // Mocking required entities
        Authentication authentication = mock(Authentication.class);
        User mockUser = mock(User.class);

        // Stubbing necessary behaviors for authentication
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser@example.com");

        // Stubbing UserService to return the mockUser
        when(userService.findUserByEmail(anyString())).thenReturn(mockUser);

        // Stubbing GutendexService to return a mock book
        BookList bookList = new BookList();
        List<Book> mockBooks = new ArrayList<>();
        mockBooks.add(new Book()); // Create a mock book object
        bookList.setResults(mockBooks);

        // Stubbing GutendexService searchBooks() to return the bookList
        when(gutendexService.searchBooks()).thenReturn(bookList);

        // Creating a mock LibBook using bookMapper
        BookMapper bookMapper = mock(BookMapper.class);
        LibBook mockBook = new LibBook(); // Create a mock LibBook object
        when(bookMapper.bookToLibBook(any())).thenReturn(mockBook);

        // Stubbing GutendexService getBookDetailsAsLibBook() to return the mockBook
        when(gutendexService.getBookDetailsAsLibBook(anyLong())).thenReturn(mockBook);

        // Mock the security context to allow the method to execute without errors
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Test the method
        assertEquals("redirect:/books/library", gutendexController.addToLibrary(model,1L, authentication));

        // Verify that userService.updateUserLibrary() is called
        verify(userService).updateUserLibrary(any());
    }

    @Test
    void testRemoveFromLibrary() {
        // Setup
        User user = new User();
        when(userService.findUserByEmail(any())).thenReturn(user);

        // Execution
        String result = gutendexController.removeFromLibrary(1L, null);

        // Verification
        assertEquals("redirect:/books/library", result);
        verify(userService, times(1)).findUserByEmail(any());
        verify(userService, times(1)).updateUserLibrary(any());
    }

    @Test
    void testReadTheBook() {
        // Test for readTheBook method
        Model mockModel = mock(Model.class);
        HttpSession mockSession = mock(HttpSession.class);
        Book mockBook = mock(Book.class);
        Format mockFormat = mock(Format.class);
        when(gutendexService.getBookDetails(anyLong())).thenReturn(mockBook);
        when(mockBook.getFormats()).thenReturn(mockFormat);
        when(gutendexService.prioritizeFormats(mockFormat)).thenReturn("pdf");
        Authentication authentication = mock(Authentication.class);
        assertEquals("books/read", gutendexController.readTheBook(mockModel, mockSession, 1,authentication));
        verify(mockSession).setAttribute(eq("bookPages"), anyList());
        verify(mockModel).addAttribute("currentPageIndex", 0);
    }

    @Test
    void testReadContentOfTheBook() {
        // Setup
        List<String> pages = new ArrayList<>();
        pages.add("Test Content");
        when(session.getAttribute("bookPages")).thenReturn(pages);
        when(model.getAttribute("currentPageIndex")).thenReturn(0);
        Authentication authentication = mock(Authentication.class);
        // Execution
        String result = gutendexController.readContentOfTheBook(model, session,authentication);

        // Verification
        assertEquals("books/read", result);
        verify(session, times(1)).getAttribute("bookPages");
        verify(model, times(1)).getAttribute("currentPageIndex");
        verify(model, times(1)).addAttribute(eq("currentPageContent"), any());
        verify(model, times(1)).addAttribute(eq("currentPageIndex"), any());
    }

    @Test
    void testNextPage() {
        // Setup
        List<String> pages = new ArrayList<>();
        pages.add("Test Content");
        when(session.getAttribute("bookPages")).thenReturn(pages);
        when(session.getAttribute("currentPageIndex")).thenReturn(0);

        // Execution
        Authentication authentication = mock(Authentication.class);
        String result = gutendexController.nextPage(model, session,authentication);

        // Verification
        assertEquals("books/read", result);
        verify(session, times(1)).getAttribute("bookPages");
        verify(session, times(1)).getAttribute("currentPageIndex");
        verify(session, times(1)).setAttribute("currentPageIndex", 1);
        verify(model, times(1)).addAttribute(eq("currentPage"), any());
        verify(model, times(1)).addAttribute(eq("totalPages"), any());
    }

    @Test
    void testPreviousPage() {
        // Setup
        List<String> pages = new ArrayList<>();
        pages.add("Test Content");
        when(session.getAttribute("bookPages")).thenReturn(pages);
        when(session.getAttribute("currentPageIndex")).thenReturn(1);

        // Execution
        Authentication authentication = mock(Authentication.class);
        String result = gutendexController.previousPage(model, session,authentication);

        // Verification
        assertEquals("books/read", result);
        verify(session, times(1)).getAttribute("bookPages");
        verify(session, times(1)).getAttribute("currentPageIndex");
        verify(session, times(1)).setAttribute("currentPageIndex", 0);
        verify(model, times(1)).addAttribute(eq("currentPage"), any());
        verify(model, times(1)).addAttribute(eq("totalPages"), any());
    }
}
