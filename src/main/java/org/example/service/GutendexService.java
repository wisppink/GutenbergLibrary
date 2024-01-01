package org.example.service;

import org.example.entity.LibBook;
import org.example.entity.User;
import org.example.mapper.BookMapper;
import org.example.repository.UserRepository;
import org.example.service.Model.Book;
import org.example.service.Model.BookList;
import org.example.service.Model.Format;
import org.example.service.imp.LibraryServiceRemoteDataSourceImp;
import org.example.service.remote.LibraryServiceRemoteDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GutendexService {

    private final LibraryServiceRemoteDataSource remoteDataSource;
    private final UserRepository userRepository;  // Inject UserRepository
    private final BookMapper bookMapper = new BookMapper();  // Initialize BookMapper
    private final Logger log = LoggerFactory.getLogger(LibraryServiceRemoteDataSourceImp.class.getName());

    @Autowired
    public GutendexService(LibraryServiceRemoteDataSource remoteDataSource, UserRepository userRepository) {
        this.remoteDataSource = remoteDataSource;
        this.userRepository = userRepository;
    }

    public BookList searchBooks() {
        BookList bookList = remoteDataSource.getBookListDefault();

        if (bookList != null) {
            return bookList;
        } else {
            Book book = new Book();
            book.setTitle("none");
            BookList emptyOne = new BookList();
            emptyOne.setResults(new ArrayList<>());
            emptyOne.getResults().add(book);
            return emptyOne;
        }
    }

    public BookList searchBooksWithFilter(int i, String param) {
        BookList bookList = remoteDataSource.getBookListWithFilter(i, param);
        if (bookList != null) {
            return bookList;
        } else {
            Book book = new Book();
            book.setTitle("none");
            BookList emptyOne = new BookList();
            emptyOne.setResults(new ArrayList<>());
            emptyOne.getResults().add(book);
            return emptyOne;
        }
    }

    public LibBook getBookDetailsAsLibBook(Long bookId) {
        log.info("get Book details as lib book");
        Book book = remoteDataSource.getBookDetail(bookId);
        LibBook libBook = bookMapper.bookToLibBook(book);
        libBook.setLastPageIndex(0);
        return libBook;
    }

    public Book getBookDetails(Long bookId) {
        log.info("getBookDetails: ");
        return remoteDataSource.getBookDetail(bookId);
    }

    public String fetchBookContent(String contentUrl) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(contentUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else if (response.statusCode() == 302) {
                // Handle redirect
                String redirectUrl = response.headers().firstValue("Location").orElse(null);
                if (redirectUrl != null) {
                    // Follow the redirect and fetch content from the final URL
                    log.info("302 " + redirectUrl);
                    return fetchBookContent(redirectUrl);
                } else {
                    System.out.println("Error: Redirect location not found");
                    return null;
                }
            } else {
                // Handle other status codes
                System.out.println("Error: HTTP request failed with status code " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            // Handle exceptions
            e.printStackTrace();
            return null;
        }
    }


    public String prioritizeFormats(Format formats) {
        List<String> prioritizedFormats = Arrays.asList("text/plain; charset=us-ascii", "text/html");
        return prioritizedFormats.stream()
                .filter(format -> formats.getFormatMap().containsKey(format))
                .findFirst()
                .orElse(null);
    }

    public List<String> splitContentIntoPages(String content) {
        log.info("content length: " + content.length());
        List<String> pages = new ArrayList<>();
        // Split content into lines
        String[] lines = content.split("\\n");
        // Define the number of lines per page
        int linesPerPage = 20;
        // Accumulate lines until reaching the desired number of lines per page
        StringBuilder currentPage = new StringBuilder();
        for (String line : lines) {
            currentPage.append(line).append("\n");
            if (currentPage.toString().split("\\n").length >= linesPerPage) {
                // Add the current page to the list and reset the StringBuilder
                pages.add(currentPage.toString());
                currentPage = new StringBuilder();
            }
        }
        // Add the last page if it's not empty
        if (!currentPage.isEmpty()) {
            pages.add(currentPage.toString());
        }
        log.info("pages size: " + pages.size());
        return pages;
    }

    public int findTheBooksLastPage(int bookID, Long userId) {
        User user = userRepository.findById(userId).orElse(null);

        // or throw an exception, return a default value, etc.
        if (user != null) {
            // Check if the book is in the user's library
            for (int i = 0; i < user.getBooks().size(); i++) {
                if (user.getBooks().get(i).getApiId() == bookID) {
                    // Retrieve the last page index from the LibBook entity
                    return user.getBooks().get(i).getLastPageIndex();
                }
            }

            // Handle the case where the book is not in the user's library
            log.warn("Book with ID {} is not in the user's library", bookID);

        } else {
            // Handle the case where the user is not found
            log.error("User not found with ID: {}", userId);
        }
        return -1; // or throw an exception, return a default value, etc.
    }
}
