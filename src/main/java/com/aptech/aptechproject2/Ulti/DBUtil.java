package com.aptech.aptechproject2.Ulti;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    // CẤU HÌNH KẾT NỐI MySQL (XAMPP)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/StoryGraph_db";
    private static final String USERNAME = "root";     // Mặc định XAMPP
    private static final String PASSWORD = "";         // Mặc định XAMPP (rỗng)

    public static Connection getConnection() throws SQLException {
        // Tải driver MySQL (tùy phiên bản)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 8.0+
            // Hoặc: Class.forName("com.mysql.jdbc.Driver"); // MySQL 5.7
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found!", e);
        }
        return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    }

    // Đóng kết nối (khuyến khích dùng)
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}