package org.example;

import org.example.LibraryService.Remote.GutendexService;
import org.example.LibraryService.Remote.LibraryServiceRemoteDataSource;
import org.example.LibraryService.Remote.LibraryServiceRemoteDataSourceImp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public LibraryServiceRemoteDataSource libraryServiceRemoteDataSource() {
        return new LibraryServiceRemoteDataSourceImp();
    }

    @Bean
    public GutendexService gutendexService(LibraryServiceRemoteDataSource remoteDataSource) {
        return new GutendexService(remoteDataSource);
    }
}
