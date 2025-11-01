package com.aptech.aptechproject2.Model;

public class Book {
    private int bookID;
    private String title;
    private String authorName;
    private int publishedYear;
    private String genres; // nối các thể loại: "Hài hước, Tình cảm"

    // Constructor đầy đủ
    public Book(int bookID, String title, String authorName, int publishedYear, String genres) {
        this.bookID = bookID;
        this.title = title;
        this.authorName = authorName;
        this.publishedYear = publishedYear;
        this.genres = genres;
    }

    // Getters
    public int getBookID() { return bookID; }
    public String getTitle() { return title; }
    public String getAuthorName() { return authorName; }
    public int getPublishedYear() { return publishedYear; }
    public String getGenres() { return genres; }

    // Setters (nếu cần)
    public void setBookID(int bookID) { this.bookID = bookID; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setPublishedYear(int publishedYear) { this.publishedYear = publishedYear; }
    public void setGenres(String genres) { this.genres = genres; }

    @Override
    public String toString() {
        return title + " - " + authorName;
    }
}