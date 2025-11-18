package com.aptech.aptechproject2.DAO;

import com.aptech.aptechproject2.Model.Review;
import com.aptech.aptechproject2.Ulti.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    public List<Review> getAllReviews() {
        List<Review> reviews = new ArrayList<>();
        String sql = """
            SELECT r.*, u.UserName, b.Title 
            FROM Review r
            JOIN `User` u ON r.UserId = u.Id
            JOIN Book b ON r.BookId = b.Id
            ORDER BY r.CreateAt DESC
            """;
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                reviews.add(extractReview(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return reviews;
    }

    public List<Review> getReviewsByBookId(int bookId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.UserName FROM review r JOIN `User` u ON r.UserId = u.Id WHERE r.BookId = ? ORDER BY r.CreateTime DESC";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) reviews.add(extractReview(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return reviews;
    }

    public double getAverageRatingForBook(int bookId) {
        String sql = "SELECT AVG(Rating) as avg_rating FROM review WHERE BookId = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    public boolean create(Review review) {
        String sql = "INSERT INTO Review (UserId, BookId, Rating, Comment) VALUES (?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, review.getUserId());
            ps.setLong(2, review.getBookId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) review.setId(rs.getLong(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(Review review) {
        String sql = "UPDATE Review SET Rating = ?, Comment = ? WHERE Id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, review.getRating());
            ps.setString(2, review.getComment());
            ps.setLong(3, review.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(long reviewId) {
        String sql = "DELETE FROM Review WHERE Id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, reviewId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Review extractReview(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setId(rs.getLong("Id"));
        r.setUserId(rs.getLong("UserId"));
        r.setBookId(rs.getLong("BookId"));
        r.setRating(rs.getInt("Rating"));
        r.setComment(rs.getString("Comment"));
        // try both column names used in different sqls
        try { r.setCreatedAt(rs.getTimestamp("CreateTime")); } catch (Exception ex) { try { r.setCreatedAt(rs.getTimestamp("CreateAt")); } catch (Exception ignored) {} }
        try { r.setUserName(rs.getString("UserName")); } catch (Exception ignored) {}
        try { r.setBookTitle(rs.getString("Title")); } catch (Exception ignored) {}
        return r;
    }
}

