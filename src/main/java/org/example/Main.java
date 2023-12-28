package org.example;

import org.example.LibraryService.Remote.GutendexService;
import org.example.LibraryService.Remote.LibraryServiceRemoteDataSource;
import org.example.LibraryService.Remote.LibraryServiceRemoteDataSourceImp;

public class Main {
    public static void main(String[] args) {
        LibraryServiceRemoteDataSource remoteDataSource = new LibraryServiceRemoteDataSourceImp();
        GutendexService gutendexService = new GutendexService(remoteDataSource);

        String queryParameters = "";

        gutendexService.searchBooks(queryParameters);
    }
}