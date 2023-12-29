package org.example.service.searchApi.remote.LibraryService.Remote;

import org.example.service.searchApi.remote.LibraryService.Remote.Model.BookList;

public interface LibraryServiceRemoteDataSource {
    BookList getBookData(String queryParameters);
}
