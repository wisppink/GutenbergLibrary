package org.example.service;

import org.example.entity.LibBook;
import org.example.mapper.BookMapper;
import org.example.service.Model.Book;
import org.example.service.Model.BookList;
import org.example.service.remote.LibraryServiceRemoteDataSource;

import java.util.ArrayList;

public class GutendexService {

    private final LibraryServiceRemoteDataSource remoteDataSource;
    BookMapper bookMapper = new BookMapper();

    public GutendexService(LibraryServiceRemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
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

    public LibBook getBookDetails(Long bookId) {
        return bookMapper.bookToLibBook(remoteDataSource.getBookDetail(bookId));
    }

}
