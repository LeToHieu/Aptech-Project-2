package com.aptech.aptechproject2.Model;

import java.time.LocalDateTime;

public class User {
    private long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String password;
    private int role;
    private LocalDateTime createTime;

    public User(long id, String username, String email, String phoneNumber, String password, int role, LocalDateTime createTime) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.createTime = createTime;
    }

    public User(String username, String email, String phonenumber, String password, int role) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phonenumber;
        this.password = password;
        this.role = role;
    }

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getRole() { return role; }
    public void setRole(int role) { this.role = role; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public String getRoleName() {
        return switch (role) {
            case 0 -> "User";
            case 1 -> "Admin";
            case 2 -> "Librarian";
            default -> "Unknown";
        };
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", role=" + getRoleName() +
                ", createTime=" + createTime +
                '}';
    }
}