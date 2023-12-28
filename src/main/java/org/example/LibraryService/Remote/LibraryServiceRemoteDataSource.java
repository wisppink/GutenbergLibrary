package org.example.LibraryService.Remote;

import org.example.LibraryService.Remote.Model.BookList;

public interface LibraryServiceRemoteDataSource {
    BookList getBookData(String queryParameters);
}
