package org.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "lib_books")
public class LibBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int apiId;
    private String url;
    private String title;
    private int lastPageIndex;

    public int getLastPageIndex() {
        return lastPageIndex;
    }

    public void setLastPageIndex(int lastPageIndex) {
        this.lastPageIndex = lastPageIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getApiId() {
        return apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
