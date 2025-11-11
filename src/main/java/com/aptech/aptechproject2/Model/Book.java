package com.aptech.aptechproject2.Model;

import java.sql.Timestamp;
import java.util.List;

public class Book {

    // Fields from the 'book' table
    private int id;
    private String title;
    private String description;
    private String image;
    private String url;
    private Timestamp createTime;
    private Timestamp updateTime;

    // Related fields
    private List<Author> authors;
    private List<Category> categories;

    // Calculated field
    private double averageRating;

    // --- Constructors ---

    public Book() {
    }

    public Book(int id, String title, String description, String image, String url, Timestamp createTime, Timestamp updateTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.url = url;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    // --- Optional: toString() for debugging ---
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", averageRating=" + averageRating +
                '}';
    }
}