package com.aptech.aptechproject2.DAO;

import com.aptech.aptechproject2.Model.Shelf;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Ulti.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShelfDAO {

    private final BookDAO bookDAO = new BookDAO();

    // === LẤY TẤT CẢ KỆ CỦA USER ===
    public List<Shelf> getShelvesByUser(long userId) {
        List<Shelf> shelves = new ArrayList<>();
        String sql = """
            SELECT s.*, u.UserName 
            FROM Shelves s
            JOIN `User` u ON s.UserId = u.Id
            WHERE s.UserId = ?
            ORDER BY s.CreateTime DESC
            """;
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Shelf shelf = extractShelf(rs);
                shelf.setBooks(getBooksInShelf(shelf.getId()));
                shelves.add(shelf);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return shelves;
    }

    // === LẤY TẤT CẢ KỆ (ADMIN) ===
    public List<Shelf> getAllShelves() {
        List<Shelf> shelves = new ArrayList<>();
        String sql = """
            SELECT s.*, u.UserName 
            FROM Shelves s
            JOIN `User` u ON s.UserId = u.Id
            ORDER BY u.UserName, s.CreateTime DESC
            """;
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Shelf shelf = extractShelf(rs);
                shelf.setBooks(getBooksInShelf(shelf.getId()));
                shelves.add(shelf);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return shelves;
    }

    // === TẠO KỆ ===
    public boolean create(Shelf shelf) {
        String sql = "INSERT INTO Shelves (Name, UserId, Description) VALUES (?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, shelf.getName());
            ps.setLong(2, shelf.getUserId());
            ps.setString(3, shelf.getDescription());
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) shelf.setId(rs.getLong(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // === CẬP NHẬT KỆ ===
    public boolean update(Shelf shelf) {
        String sql = "UPDATE Shelves SET Name = ?, Description = ? WHERE Id = ? AND UserId = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, shelf.getName());
            ps.setString(2, shelf.getDescription());
            ps.setLong(3, shelf.getId());
            ps.setLong(4, shelf.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // === XÓA KỆ (XÓA CẢ SÁCH TRONG KỆ) ===
    public boolean delete(long shelfId, long userId) {
        String sql = "DELETE FROM Shelves WHERE Id = ? AND UserId = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, shelfId);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // === THÊM SÁCH VÀO KỆ ===
    public boolean addBookToShelf(long shelfId, long bookId) {
        String sql = "INSERT IGNORE INTO BookShelves (ShelvesId, BookId) VALUES (?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, shelfId);
            ps.setLong(2, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // === XÓA SÁCH KHỎI KỆ ===
    public boolean removeBookFromShelf(long shelfId, long bookId) {
        String sql = "DELETE FROM BookShelves WHERE ShelvesId = ? AND BookId = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, shelfId);
            ps.setLong(2, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // === LẤY SÁCH TRONG KỆ ===
    private List<Book> getBooksInShelf(long shelfId) {
        List<Book> books = new ArrayList<>();
        String sql = """
            SELECT b.* FROM Book b
            JOIN BookShelves bs ON b.Id = bs.BookId
            WHERE bs.ShelvesId = ?
            """;
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, shelfId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                books.add(bookDAO.extractBook(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return books;
    }

    private Shelf extractShelf(ResultSet rs) throws SQLException {
        Shelf s = new Shelf();
        s.setId(rs.getLong("Id"));
        s.setName(rs.getString("Name"));
        s.setUserId(rs.getLong("UserId"));
        s.setDescription(rs.getString("Description"));
        s.setCreateTime(rs.getTimestamp("CreateTime"));
        s.setUserName(rs.getString("UserName"));
        return s;
    }
}