package com.aptech.aptechproject2.DAO;

import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Ulti.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // LẤY TẤT CẢ SÁCH (có JOIN với Authors và BookGenres)
    public List<Book> getAll() {
        List<Book> books = new ArrayList<>();
        String sql = """
            SELECT
                b.BookID,
                b.Title,
                a.AuthorName,
                b.PublishedYear,
                GROUP_CONCAT(g.GenreName ORDER BY g.GenreName SEPARATOR ', ') AS Genres
            FROM Books b
            JOIN Authors a ON b.AuthorID = a.AuthorID
            LEFT JOIN BookGenres bg ON b.BookID = bg.BookID
            LEFT JOIN Genres g ON bg.GenreID = g.GenreID
            GROUP BY b.BookID
            ORDER BY b.Title
            """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("BookID"),
                        rs.getString("Title"),
                        rs.getString("AuthorName"),
                        rs.getInt("PublishedYear"),
                        rs.getString("Genres") != null ? rs.getString("Genres") : "Không có thể loại"
                );
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    // TÌM THEO ID (nếu cần sau này)
    public Book getById(int bookId) {
        String sql = """
            SELECT
                b.BookID, b.Title, a.AuthorName, b.PublishedYear,
                GROUP_CONCAT(g.GenreName SEPARATOR ', ') AS Genres
            FROM Books b
            JOIN Authors a ON b.AuthorID = a.AuthorID
            LEFT JOIN BookGenres bg ON b.BookID = bg.BookID
            LEFT JOIN Genres g ON bg.GenreID = g.GenreID
            WHERE b.BookID = ?
            GROUP BY b.BookID
            """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Book(
                        rs.getInt("BookID"),
                        rs.getString("Title"),
                        rs.getString("AuthorName"),
                        rs.getInt("PublishedYear"),
                        rs.getString("Genres") != null ? rs.getString("Genres") : "Không có thể loại"
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}