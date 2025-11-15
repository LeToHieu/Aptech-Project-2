package com.aptech.aptechproject2.DAO;

import com.aptech.aptechproject2.Model.Borrow;
import com.aptech.aptechproject2.Ulti.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {

    public List<Borrow> getAllBorrows() {
        List<Borrow> borrows = new ArrayList<>();
        String sql = """
            SELECT b.*, u.UserName, bk.Title 
            FROM Borrow b
            JOIN `User` u ON b.UserId = u.Id
            JOIN Book bk ON b.BookId = bk.Id
            ORDER BY b.BorrowDay DESC
            """;
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                borrows.add(extractBorrow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return borrows;
    }

    public boolean create(long userId, long bookId) {
        String sql = "INSERT INTO Borrow (UserId, BookId) VALUES (?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean returnBook(long borrowId) {
        String sql = "UPDATE Borrow SET ReturnDateTime = NOW(), Status = 1 WHERE Id = ? AND Status = 0";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, borrowId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public void updateOverdueStatus() {
        String sql = "UPDATE Borrow SET Status = 2 WHERE ExpireDay < NOW() AND Status = 0";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean delete(long borrowId) {
        String sql = "DELETE FROM Borrow WHERE Id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, borrowId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Borrow extractBorrow(ResultSet rs) throws SQLException {
        Borrow b = new Borrow();
        b.setId(rs.getLong("Id"));
        b.setBorrowDay(rs.getTimestamp("BorrowDay"));
        b.setExpireDay(rs.getTimestamp("ExpireDay"));
        b.setReturnDateTime(rs.getTimestamp("ReturnDateTime"));
        b.setUserId(rs.getLong("UserId"));
        b.setBookId(rs.getLong("BookId"));
        b.setStatus(rs.getInt("Status"));
        b.setUserName(rs.getString("UserName"));
        b.setBookTitle(rs.getString("Title"));
        return b;
    }
}