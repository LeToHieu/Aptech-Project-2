package com.aptech.aptechproject2.DAO;

import com.aptech.aptechproject2.Model.Category;
import com.aptech.aptechproject2.Ulti.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT Id, Name, Description FROM Category ORDER BY Id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(extractCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public List<Category> getCategoriesByBookId(int bookId) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.Id, c.Name, c.Description FROM category c " +
                "JOIN bookcategory bc ON c.Id = bc.CategoryId WHERE bc.BookId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categories.add(extractCategory(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public boolean create(Category category) {
        String sql = "INSERT INTO Category (Name, Description) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    category.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Category category) {
        String sql = "UPDATE Category SET Name = ?, Description = ? WHERE Id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, category.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Category WHERE Id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Category extractCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("Id"));
        category.setName(rs.getString("Name"));
        category.setDescription(rs.getString("Description"));
        return category;
    }
}