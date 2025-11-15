package com.aptech.aptechproject2.Model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Shelf {
    private long id;
    private String name;
    private long userId;
    private String description;
    private Timestamp createTime;
    private String userName;
    private List<Book> books = new ArrayList<>();

    // Constructors
    public Shelf() {}
    public Shelf(long id, String name, long userId, String description, Timestamp createTime) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.description = description;
        this.createTime = createTime;
    }

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }

    public int getBookCount() { return books.size(); }

    @Override
    public String toString() {
        return name + " (" + getBookCount() + " sách)";
    }
}