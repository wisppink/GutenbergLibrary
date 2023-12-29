package org.example.ui;

import org.example.service.searchApi.local.SearchBookListResponse;
import org.example.service.searchApi.local.SearchBookResponse;
import org.example.service.searchApi.remote.LibraryService.Remote.Model.BookList;

import java.util.ArrayList;
import java.util.List;

public class UiMapper {
    public SearchBookListResponse RemoteToLocalBookList(BookList bookList) {
        SearchBookListResponse response = new SearchBookListResponse();
        response.setCount(bookList.getCount());
        response.setPrevious(bookList.getPrevious());
        response.setNext(bookList.getNext());
        List<SearchBookResponse> searchBookResponseList = new ArrayList<>();

        for (int i = 0; i < bookList.getResults().size(); i++) {
            SearchBookResponse bookResponse = new SearchBookResponse();
            bookResponse.setAuthors(bookList.getResults().get(i).getAuthors());
            bookResponse.setBookshelves(bookList.getResults().get(i).getBookshelves());
            bookResponse.setCopyright(bookList.getResults().get(i).getCopyright());
            bookResponse.setId(bookList.getResults().get(i).getId());
            bookResponse.setFormats(bookList.getResults().get(i).getFormats());
            bookResponse.setLanguages(bookList.getResults().get(i).getLanguages());
            bookResponse.setTranslators(bookList.getResults().get(i).getTranslators());
            bookResponse.setSubjects(bookList.getResults().get(i).getSubjects());
            bookResponse.setTitle(bookList.getResults().get(i).getTitle());
            bookResponse.setDownload_count(bookList.getResults().get(i).getDownload_count());
            bookResponse.setMedia_type(bookList.getResults().get(i).getMedia_type());

            searchBookResponseList.add(bookResponse);
        }
        response.setResults(searchBookResponseList);
        return response;
    }
}
