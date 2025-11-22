package com.aptech.aptechproject2.DAO;

import com.aptech.aptechproject2.Model.Author;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.Category;
import com.aptech.aptechproject2.Ulti.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book ORDER BY CreateTime DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Book book = extractBook(rs);
                book.setAuthors(getAuthorsForBook(book.getId()));
                book.setCategories(getCategoriesForBook(book.getId()));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> getLatestBooks(int limit) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book ORDER BY CreateTime DESC LIMIT ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book book = extractBook(rs);
                book.setAuthors(getAuthorsForBook(book.getId()));
                book.setCategories(getCategoriesForBook(book.getId()));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> searchBooksByTitle(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE Title LIKE ? ORDER BY CreateTime DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book book = extractBook(rs);
                book.setAuthors(getAuthorsForBook(book.getId()));
                book.setCategories(getCategoriesForBook(book.getId()));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> searchBooks(String keyword, List<Author> authors, List<Category> categories) {
        List<Book> books = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT b.* FROM book b ");
        if (!authors.isEmpty()) {
            sql.append("JOIN bookauthor ba ON b.Id = ba.BookId ");
        }
        if (!categories.isEmpty()) {
            sql.append("JOIN bookcategory bc ON b.Id = bc.BookId ");
        }
        sql.append("WHERE 1=1 ");
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND (b.Title LIKE ? OR b.Description LIKE ?) ");
        }
        if (!authors.isEmpty()) {
            sql.append("AND ba.AuthorId IN (");
            for (int i = 0; i < authors.size(); i++) {
                sql.append("?");
                if (i < authors.size() - 1) sql.append(",");
            }
            sql.append(") ");
        }
        if (!categories.isEmpty()) {
            sql.append("AND bc.CategoryId IN (");
            for (int i = 0; i < categories.size(); i++) {
                sql.append("?");
                if (i < categories.size() - 1) sql.append(",");
            }
            sql.append(") ");
        }
        sql.append("ORDER BY b.CreateTime DESC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (keyword != null && !keyword.isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword + "%");
                ps.setString(paramIndex++, "%" + keyword + "%");
            }
            for (Author author : authors) {
                ps.setInt(paramIndex++, author.getId());
            }
            for (Category category : categories) {
                ps.setInt(paramIndex++, category.getId());
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book book = extractBook(rs);
                book.setAuthors(getAuthorsForBook(book.getId()));
                book.setCategories(getCategoriesForBook(book.getId()));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public boolean create(Book book) {
        String sql = "INSERT INTO book (Title, Description, Image, Url) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getDescription());
            stmt.setString(3, book.getImage());
            stmt.setString(4, book.getUrl());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    book.setId(rs.getInt(1));
                }
                insertBookAuthors(book.getId(), book.getAuthors());
                insertBookCategories(book.getId(), book.getCategories());
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Book book) {
        String sql = "UPDATE book SET Title = ?, Description = ?, Image = ?, Url = ? WHERE Id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getDescription());
            stmt.setString(3, book.getImage());
            stmt.setString(4, book.getUrl());
            stmt.setInt(5, book.getId());

            boolean updated = stmt.executeUpdate() > 0;
            if (updated) {
                // Delete old relations
                deleteBookAuthors(book.getId());
                deleteBookCategories(book.getId());
                // Insert new
                insertBookAuthors(book.getId(), book.getAuthors());
                insertBookCategories(book.getId(), book.getCategories());
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        try (Connection conn = DBUtil.getConnection()) {
            // Delete relations first
            deleteBookAuthors(id);
            deleteBookCategories(id);

            String sql = "DELETE FROM book WHERE Id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void insertBookAuthors(int bookId, List<Author> authors) {
        if (authors.isEmpty()) return;
        String sql = "INSERT INTO bookauthor (BookId, AuthorId) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Author author : authors) {
                stmt.setInt(1, bookId);
                stmt.setInt(2, author.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertBookCategories(int bookId, List<Category> categories) {
        if (categories.isEmpty()) return;
        String sql = "INSERT INTO bookcategory (BookId, CategoryId) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Category category : categories) {
                stmt.setInt(1, bookId);
                stmt.setInt(2, category.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteBookAuthors(int bookId) {
        String sql = "DELETE FROM bookauthor WHERE BookId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteBookCategories(int bookId) {
        String sql = "DELETE FROM bookcategory WHERE BookId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Author> getAuthorsForBook(int bookId) {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT a.* FROM author a JOIN bookauthor ba ON a.Id = ba.AuthorId WHERE ba.BookId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                authors.add(new Author(rs.getInt("Id"), rs.getString("Name"), rs.getString("Description"), rs.getString("Image")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors;
    }

    private List<Category> getCategoriesForBook(int bookId) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.* FROM Category c JOIN bookcategory bc ON c.Id = bc.CategoryId WHERE bc.BookId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categories.add(new Category(rs.getInt("Id"), rs.getString("Name"), rs.getString("Description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    Book extractBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("Id"));
        book.setTitle(rs.getString("Title"));
        book.setDescription(rs.getString("Description"));
        book.setImage(rs.getString("Image"));
        book.setUrl(rs.getString("Url"));
        book.setCreateTime(rs.getTimestamp("CreateTime"));
        book.setUpdateTime(rs.getTimestamp("UpdateTime"));
        return book;
    }
}