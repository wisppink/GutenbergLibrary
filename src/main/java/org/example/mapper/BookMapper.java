package org.example.mapper;

import org.example.entity.LibBook;
import org.example.service.Model.Book;

import java.util.logging.Logger;

public class BookMapper {
    Logger logger = Logger.getLogger(BookMapper.class.getName());

    public LibBook bookToLibBook(Book book) {
        LibBook libBook = new LibBook();
        logger.info("BookId: " + book.getId());
        libBook.setApiId(book.getId());
        libBook.setUrl("gutendex.com/books/?ids=" + book.getId());
        logger.info(book.getTitle());
        libBook.setTitle(book.getTitle());
        return libBook;
    }
}
