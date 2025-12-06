package com.aptech.aptechproject2.Model;

import java.sql.Timestamp;

public class Borrow {
    private long id;
    private Timestamp borrowDay;
    private Timestamp expireDay;
    private Timestamp returnDateTime;
    private long userId;
    private long bookId;
    private int status; // 0: Đang mượn, 1: Đã trả, 2: Quá hạn
    private String userName;
    private String bookTitle;

    // Constructors
    public Borrow() {}
    public Borrow(int id, Timestamp borrowDay, Timestamp expireDay, Timestamp returnDateTime,
                  int userId, int bookId, int status) {
        this.id = id;
        this.borrowDay = borrowDay;
        this.expireDay = expireDay;
        this.returnDateTime = returnDateTime;
        this.userId = userId;
        this.bookId = bookId;
        this.status = status;
    }

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public Timestamp getBorrowDay() { return borrowDay; }
    public void setBorrowDay(Timestamp borrowDay) { this.borrowDay = borrowDay; }
    public Timestamp getExpireDay() { return expireDay; }
    public void setExpireDay(Timestamp expireDay) { this.expireDay = expireDay; }
    public Timestamp getReturnDateTime() { return returnDateTime; }
    public void setReturnDateTime(Timestamp returnDateTime) { this.returnDateTime = returnDateTime; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public long getBookId() { return bookId; }
    public void setBookId(long bookId) { this.bookId = bookId; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getStatusName() {
        return switch (status) {
            case 0 -> "Đang chờ duyệt";
            case 1 -> "Đang mượn";
            case 2 -> "Bị từ chối";
            case 3 -> "Quá hạn";
            case 4 -> "Đã trả";
            default -> "Không xác định";
        };
    }

    // Thêm vào class Borrow.java
    public String getBorrowDayFormatted() {
        return borrowDay != null ?
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        .format(borrowDay.toLocalDateTime()) : "";
    }

    public String getExpireDayFormatted() {
        return expireDay != null ?
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        .format(expireDay.toLocalDateTime()) : "";
    }

    @Override
    public String toString() {
        return "Borrow{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", status=" + getStatusName() +
                '}';
    }
}