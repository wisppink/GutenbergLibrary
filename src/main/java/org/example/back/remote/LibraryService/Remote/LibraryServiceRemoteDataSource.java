package org.example.back.remote.LibraryService.Remote;

import org.example.back.remote.LibraryService.Remote.Model.BookList;

public interface LibraryServiceRemoteDataSource {
    BookList getBookData(String queryParameters);
}
