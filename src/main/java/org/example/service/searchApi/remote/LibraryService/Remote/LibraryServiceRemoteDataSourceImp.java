package org.example.service.searchApi.remote.LibraryService.Remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.service.searchApi.remote.LibraryService.Remote.Model.BookList;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
    public BookList getBookData(String queryParameters) {
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

}
