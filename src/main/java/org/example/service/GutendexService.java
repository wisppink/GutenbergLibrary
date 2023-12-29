package org.example.service;

import org.example.service.Model.Book;
import org.example.service.Model.BookList;
import org.example.service.remote.LibraryServiceRemoteDataSource;

import java.util.ArrayList;

public class GutendexService {

    private final LibraryServiceRemoteDataSource remoteDataSource;

    public GutendexService(LibraryServiceRemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
    }

    public BookList searchBooks() {
        BookList bookList = remoteDataSource.getBooklistDefault();

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

    public BookList searchBooksWithFilter(int i, String param) {
        BookList bookList = remoteDataSource.getBookListWithFilter(i, param);
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
