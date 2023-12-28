package org.example.LibraryService.Remote;

import org.example.LibraryService.Remote.Model.Book;
import org.example.LibraryService.Remote.Model.BookList;

import java.util.List;

public class GutendexService {

    private final LibraryServiceRemoteDataSource remoteDataSource;

    public GutendexService(LibraryServiceRemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
    }

    public void searchBooks(String queryParameters) {
        BookList bookList = remoteDataSource.getBookData(queryParameters);
        if (bookList != null) {
            // Now you can work with the parsed BookList object

            int bookCount = bookList.getCount();
            System.out.println("Total books: " + bookCount);

            // You can iterate through the 'results' list to get details of each book
            List<Book> books = bookList.getResults();
            for (Book book : books) {
                String title = book.getTitle();
                System.out.println("Title: " + title);
            }
        } else {
            System.out.println("The API request failed.");
        }
    }

}
