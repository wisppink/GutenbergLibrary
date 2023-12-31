package org.example.mapper;

import org.example.entity.LibBook;
import org.example.service.Model.Book;

public class BookMapper {
    public LibBook bookToLibBook(Book book) {
        LibBook libBook = new LibBook();
        libBook.setId((long) book.getId());
        libBook.setUrl("gutendex.com/books/?id=" + book.getId());
        return libBook;
    }
}
