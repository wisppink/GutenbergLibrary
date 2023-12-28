package org.example.back.remote.LibraryService.Remote;

import org.example.back.remote.LibraryService.Remote.Model.Book;
import org.example.back.remote.LibraryService.Remote.Model.BookList;

import java.util.ArrayList;

public class GutendexService {

    private final LibraryServiceRemoteDataSource remoteDataSource;

    public GutendexService(LibraryServiceRemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
    }

    public BookList searchBooks(String queryParameters) {
        BookList bookList = remoteDataSource.getBookData(queryParameters);

        if (bookList != null) {
            return bookList;
        } else {
            Book book = new Book();
            book.setTitle("none");
            BookList emptyOne = new BookList();
            emptyOne.setResults(new ArrayList<Book>());
            emptyOne.getResults().add(book);
            return emptyOne;
        }
    }
}
