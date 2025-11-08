package com.aptech.aptechproject2.DAO;

import com.aptech.aptechproject2.Model.User;
import com.aptech.aptechproject2.Ulti.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public User getByEmail(String email) {
        String sql = "SELECT * FROM User WHERE Email = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("UserID"),
                        rs.getString("Username"),
                        rs.getString("Email"),
                        rs.getString("PasswordHash")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean create(User user) {
        String sql = "INSERT INTO User (Username, Email, PasswordHash) VALUES(?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("UserID"),
                        rs.getString("Username"),
                        rs.getString("Email"),
                        rs.getString("PasswordHash")
                );
                user.setFullName(rs.getString("FullName"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}