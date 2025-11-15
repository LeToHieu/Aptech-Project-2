package com.aptech.aptechproject2.DAO;

import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.Shelves;
import com.aptech.aptechproject2.Ulti.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShelvesDAO {

    public List<Shelves> getShelvesByUserId(int userId) {
        List<Shelves> shelves = new ArrayList<>();
        String sql = "SELECT Id, Name, Description, CreateTime FROM shelves WHERE UserId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Shelves shelf = new Shelves();
                shelf.setId(rs.getInt("Id"));
                shelf.setName(rs.getString("Name"));
                shelf.setDescription(rs.getString("Description"));
                shelf.setCreateTime(rs.getTimestamp("CreateTime"));
                shelf.setUserId(userId);
                shelves.add(shelf);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shelves;
    }

    public List<Book> getBooksByShelfId(int shelfId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.* FROM book b JOIN bookshelves bs ON b.Id = bs.BookId WHERE bs.ShelvesId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shelfId);
            ResultSet rs = ps.executeQuery();
            BookDAO bookDAO = new BookDAO();
            while (rs.next()) {
                books.add(bookDAO.extractBook(rs)); // Sử dụng extractBook từ BookDAO
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}