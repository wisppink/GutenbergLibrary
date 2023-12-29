package org.example.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.service.Model.BookList;
import org.example.service.remote.LibraryServiceRemoteDataSource;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class LibraryServiceRemoteDataSourceImp implements LibraryServiceRemoteDataSource {
    private final String baseUrl;

    public LibraryServiceRemoteDataSourceImp() {
        this.baseUrl = "https://gutendex.com/books/";
    }

    private static <T> T parseJsonResponse(String jsonResponse, Class<T> valueType) throws Exception {
        // Create an instance of ObjectMapper to parse JSON
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, valueType);
    }

    @Override
    public BookList getBooklistDefault() {
        try {
            // Create an instance of HttpClient
            try (HttpClient client = HttpClient.newHttpClient()) {

                // Create an HttpRequest to the API URL
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl)) // Use the class member variable apiUrl
                        .GET().build();

                // Send the request and get the response
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Check if the request was successful (status code 200)
                if (response.statusCode() == 200) {
                    // Parse the JSON response into the BookList class
                    return parseJsonResponse(response.body(), BookList.class);
                } else if (response.statusCode() == 301) {
                    // Handle redirect by extracting the new location from the "Location" header
                    String newLocation = response.headers().firstValue("Location").orElse(null);
                    if (newLocation != null) {
                        // Create a new request to the redirected URL
                        HttpRequest redirectRequest = HttpRequest.newBuilder().uri(URI.create(newLocation)).GET().build();

                        // Send the redirected request and get the response
                        HttpResponse<String> redirectResponse = client.send(redirectRequest, HttpResponse.BodyHandlers.ofString());

                        // Process the redirected response as needed
                        // ...

                    } else {
                        System.out.println("Error: Redirect location not found");
                    }
                } else {
                    System.out.println("Error: HTTP request failed with status code " + response.statusCode());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // You might want to return something meaningful here
    }

    //get books with filters
    @Override
    public BookList getBookListWithFilter(int i, String param) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            String filter = "";
            switch (i) {
                case 0:
                    // Filter for books with at least one author alive in a given range of years
                    filter = "author_year_start=" + param + "&author_year_end=" + param;
                    break;
                case 1:
                    // Filter for books with a certain copyright status
                    filter = "copyright=" + param;
                    break;
                case 2:
                    // Filter for books with Project Gutenberg ID numbers in a given list of numbers
                    filter = "ids=" + param;
                    break;
                case 3:
                    // Filter for books in any of a list of languages
                    filter = "languages=" + param;
                    break;
                case 4:
                    // Filter for books with a given MIME type
                    filter = "mime_type=" + param;
                    break;
                case 5:
                    // Filter to search author names and book titles with given words
                    filter = "search=" + param.replace(" ", "%20");
                    break;
                case 6:
                    // Filter to sort books
                    filter = "sort=" + param;
                    break;
                case 7:
                    // Filter to search for a case-insensitive key-phrase in books' bookshelves or subjects
                    filter = "topic=" + param;
                    break;
                default:
                    getBooklistDefault();
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "?" + filter))
                    .GET()
                    .build();


            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if the request was successful (status code 200)
            if (response.statusCode() == 200) {
                // Parse the JSON response into the BookList class
                return parseJsonResponse(response.body(), BookList.class);
            } else if (response.statusCode() == 301) {
                // Handle redirect by extracting the new location from the "Location" header
                String newLocation = response.headers().firstValue("Location").orElse(null);
                if (newLocation != null) {
                    // Create a new request to the redirected URL
                    HttpRequest redirectRequest = HttpRequest.newBuilder().uri(URI.create(newLocation)).GET().build();

                    // Send the redirected request and get the response
                    HttpResponse<String> redirectResponse = client.send(redirectRequest, HttpResponse.BodyHandlers.ofString());

                    // Process the redirected response as needed
                    // ...

                } else {
                    System.out.println("Error: Redirect location not found");
                }
            } else {
                System.out.println("Error: HTTP request failed with status code " + response.statusCode());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return null; // You might want to return something meaningful here
    }
}
