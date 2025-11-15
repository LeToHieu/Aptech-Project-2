package com.aptech.aptechproject2.Model;

import java.sql.Timestamp;

public class Shelves {
    private int id;
    private String name;
    private int userId;
    private String description;
    private Timestamp createTime;

    // Getters/Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }
}