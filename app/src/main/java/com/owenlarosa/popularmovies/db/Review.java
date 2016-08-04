package com.owenlarosa.popularmovies.db;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "REVIEW".
 */
public class Review {

    private Long id;
    private String author;
    private String content;

    public Review() {
    }

    public Review(Long id) {
        this.id = id;
    }

    public Review(Long id, String author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
