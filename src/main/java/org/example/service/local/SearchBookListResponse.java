package org.example.service.local;

import java.util.List;

public class SearchBookListResponse {
    public int count;
    public String next;
    public String previous;
    public List<SearchBookResponse> results;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<SearchBookResponse> getResults() {
        return results;
    }

    public void setResults(List<SearchBookResponse> results) {
        this.results = results;
    }
}
