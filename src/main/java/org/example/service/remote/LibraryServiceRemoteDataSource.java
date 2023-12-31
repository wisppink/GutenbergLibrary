package org.example.service.remote;

import org.example.service.Model.Book;
import org.example.service.Model.BookList;

public interface LibraryServiceRemoteDataSource {
    BookList getBookListDefault();

    BookList getBookListWithFilter(int i, String param);

    Book getBookDetail(Long bookId);
}
