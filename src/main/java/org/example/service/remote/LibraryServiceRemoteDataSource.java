package org.example.service.remote;

import org.example.service.Model.BookList;

public interface LibraryServiceRemoteDataSource {
    BookList getBooklistDefault();

    BookList getBookListWithFilter(int i, String param);
}
