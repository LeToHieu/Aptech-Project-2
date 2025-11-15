package com.aptech.aptechproject2.Model;

import java.sql.Timestamp;

public class Review {
    private long id;
    private long userId;
    private long bookId;
    private int rating; // 1-5
    private String comment;
    private Timestamp createdAt;
    private String userName;
    private String bookTitle;

    // Constructors
    public Review() {}
    public Review(long id, long userId, long bookId, int rating, String comment, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public long getBookId() { return bookId; }
    public void setBookId(long bookId) { this.bookId = bookId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getRatingStars() {
        return "★★★★★".substring(0, rating) + "☆☆☆☆☆".substring(rating);
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                '}';
    }
}