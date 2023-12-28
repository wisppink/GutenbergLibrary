package org.example.back.repo;

import org.example.back.remote.LibraryService.Remote.GutendexService;
import org.example.back.remote.LibraryService.Remote.LibraryServiceRemoteDataSource;
import org.example.back.remote.LibraryService.Remote.LibraryServiceRemoteDataSourceImp;
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
