package org.example.back.repo;

import org.example.back.remote.LibraryService.Remote.GutendexService;
import org.example.back.remote.LibraryService.Remote.LibraryServiceRemoteDataSource;
import org.example.back.remote.LibraryService.Remote.LibraryServiceRemoteDataSourceImp;

public class Main {
    public static void main(String[] args) {
        LibraryServiceRemoteDataSource remoteDataSource = new LibraryServiceRemoteDataSourceImp();
        GutendexService gutendexService = new GutendexService(remoteDataSource);

        String queryParameters = "";

        gutendexService.searchBooks(queryParameters);
    }
}