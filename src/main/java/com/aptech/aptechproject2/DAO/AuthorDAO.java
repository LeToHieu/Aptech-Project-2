package com.aptech.aptechproject2.DAO;

import com.aptech.aptechproject2.Model.Author;
import com.aptech.aptechproject2.Ulti.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO {

    public List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT Id, Name, Description, Image FROM author";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("Id");
                String name = rs.getString("Name");
                String description = rs.getString("Description");
                String image = rs.getString("Image");
                authors.add(new Author(id, name, description, image));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors;
    }

    public boolean create(Author author) {
        String sql = "INSERT INTO author (Name, Description, Image) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, author.getName());
            stmt.setString(2, author.getDescription());
            stmt.setString(3, author.getImage());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    author.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Author author) {
        String sql = "UPDATE author SET Name = ?, Description = ?, Image = ? WHERE Id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, author.getName());
            stmt.setString(2, author.getDescription());
            stmt.setString(3, author.getImage());
            stmt.setInt(4, author.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM author WHERE   = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}