package org.example.controller;

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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

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
        // Setup
        when(gutendexService.searchBooks()).thenReturn(new BookList());

        // Execution
        String result = String.valueOf(gutendexController.searchBooks(model, null));

        // Verification
        assertEquals("books/results", result);
        verify(gutendexService, times(1)).searchBooks();
        verify(model, times(1)).addAttribute(eq("searchResults"), any());
    }

    @Test
    void testGetSearchFilterInput() {
        // Execution
        String result = gutendexController.getSearchFilterInput(model, null);

        // Verification
        assertEquals("books/searchFilterInput", result);
        verifyNoInteractions(gutendexService);
        verify(model, times(1)).addAttribute(eq("username"), any());
    }

    @Test
    void testHandleSearchForm() {
        // Setup
        int switchCase = 1;
        String inputString = "Test Input";

        // Execution
        String result = gutendexController.handleSearchForm(model, switchCase, inputString, session, null);

        // Verification
        assertEquals("redirect:/books/results", result);
        verify(session, times(1)).setAttribute("switchCase", switchCase);
        verify(session, times(1)).setAttribute("inputString", inputString);
        verifyNoInteractions(gutendexService);
        verify(model, times(1)).addAttribute(eq("username"), any());
    }

    @Test
    void testShowResults() {
        // Setup
        when(session.getAttribute("switchCase")).thenReturn(1);
        when(session.getAttribute("inputString")).thenReturn("Test Input");
        when(gutendexService.searchBooksWithFilter(1, "Test Input")).thenReturn(new BookList());

        // Execution
        String result = gutendexController.showResults(model, session, null);

        // Verification
        assertEquals("books/results", result);
        verify(session, times(1)).getAttribute("switchCase");
        verify(session, times(1)).getAttribute("inputString");
        verify(gutendexService, times(1)).searchBooksWithFilter(1, "Test Input");
        verify(model, times(1)).addAttribute(eq("searchResults"), any());
        verify(model, times(1)).addAttribute(eq("switchCase"), eq(1));
        verify(model, times(1)).addAttribute(eq("inputString"), eq("Test Input"));
        verify(model, times(1)).addAttribute(eq("username"), any());
    }

    @Test
    void testShowLibrary() {
        // Setup
        User user = new User();
        List<LibBook> library = new ArrayList<>();
        library.add(new LibBook()); // Add some sample book
        user.setBooks(library);
        when(userService.findUserByEmail(any())).thenReturn(user);

        // Execution
        String result = gutendexController.showLibrary(model, null);

        // Verification
        assertEquals("books/library", result);
        verify(userService, times(1)).findUserByEmail(any());
        verify(model, times(1)).addAttribute(eq("username"), any());
        verify(model, times(1)).addAttribute(eq("library"), eq(library));
    }

    @Test
    void testAddToLibrary() {
        // Setup
        User user = new User();
        LibBook book = new LibBook();
        when(userService.findUserByEmail(any())).thenReturn(user);
        when(gutendexService.getBookDetailsAsLibBook(anyLong())).thenReturn(book);

        // Execution
        String result = gutendexController.addToLibrary(1L, null);

        // Verification
        assertEquals("redirect:/books/library", result);
        verify(userService, times(1)).findUserByEmail(any());
        verify(gutendexService, times(1)).getBookDetailsAsLibBook(anyLong());
        verify(userService, times(1)).updateUserLibrary(any());
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
        // Setup
        Book book = new Book();
        Format formats = new Format();
        formats.getFormatMap().put("TestFormat", "TestURL");
        book.setFormats(formats);
        when(gutendexService.getBookDetails(1L)).thenReturn(book);
        when(gutendexService.fetchBookContent(anyString())).thenReturn("Test Content");
        when(gutendexService.splitContentIntoPages(anyString())).thenReturn(new ArrayList<>());

        // Execution
        String result = gutendexController.readTheBook(model, session, 1);

        // Verification
        assertEquals("books/read", result);
        verify(gutendexService, times(1)).getBookDetails(1L);
        verify(gutendexService, times(1)).fetchBookContent(anyString());
        verify(gutendexService, times(1)).splitContentIntoPages(anyString());
        verify(session, times(1)).setAttribute(eq("bookPages"), any());
        verify(model, times(1)).addAttribute(eq("currentPageIndex"), any());
    }

    @Test
    void testReadContentOfTheBook() {
        // Setup
        List<String> pages = new ArrayList<>();
        pages.add("Test Content");
        when(session.getAttribute("bookPages")).thenReturn(pages);
        when(model.getAttribute("currentPageIndex")).thenReturn(0);

        // Execution
        String result = gutendexController.readContentOfTheBook(model, session);

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
        String result = gutendexController.nextPage(model, session);

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
        String result = gutendexController.previousPage(model, session);

        // Verification
        assertEquals("books/read", result);
        verify(session, times(1)).getAttribute("bookPages");
        verify(session, times(1)).getAttribute("currentPageIndex");
        verify(session, times(1)).setAttribute("currentPageIndex", 0);
        verify(model, times(1)).addAttribute(eq("currentPage"), any());
        verify(model, times(1)).addAttribute(eq("totalPages"), any());
    }
}
